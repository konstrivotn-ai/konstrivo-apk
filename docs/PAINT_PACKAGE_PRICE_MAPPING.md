# Paint Package → MaterialPrice Mapping (Design Spec)

Purpose
-------
Document how future `PaintPackage` entities will map to existing `MaterialPrice` records without breaking the current pricing system. This is a conceptual design only — no schema changes, no migrations, no code edits are performed here.

Principles / Invariants
-----------------------
- Keep responsibilities separate:
  - `PaintProduct` = formulation / technical metadata (coverage, coats, etc.)
  - `PaintPackage` = exact purchasable SKU (volume or weight, supplier SKU, country availability)
  - `MaterialPrice` = price record (country, supplier, currency, effective date, source, fiscal profile)
- Never convert units (KG ↔ L) without an authoritative density.
- Do not embed package identity inside `MaterialPrice` legacy fields (no composite keys, no hidden supplierReference hacks).

1. Identity
-----------

PaintProduct
- Identity: `productId` — formulation-level canonical identifier.
- Holds: coverage unit/value, recommended coats, formulation notes.

PaintPackage
- Identity: `packageId` — unique SKU identifier for a packaged variant of a product.
- Holds: `productId` (FK to formulation), `containerVolumeL?`, `containerWeightKg?`, `supplierReference?`, `countryCode?`, `active`.

MaterialPrice
- Identity: the existing price record (no schema change described here). Important fields include `materialId` (current key), `country`, `supplier`, `currency`, `effectiveFrom`/`effectiveTo`, `source`, `fiscalProfile`, `price`.
- MaterialPrice may need to reference `packageId` in the long-term design (see mapping), but legacy rows must remain valid.

2. Required future mapping (conceptual)
-------------------------------------
Conceptual target relationship:

```
PaintPackage.packageId  → MaterialPrice.packageId
```

This means a MaterialPrice row can be associated with a `packageId` for explicit SKU-level pricing. MaterialPrice rows that predate this design (legacy rows) remain valid and should be supported by the lookup algorithm.

3. Backward compatibility
-------------------------

Legacy lookup behavior (today):
- `MaterialPrice` rows are looked up by `materialId` (or equivalent) which maps to the project’s current material identifier. Prices are matched by country, supplier, currency, and effective date as implemented today.

New package-aware behavior (future):
- Prefer an exact `MaterialPrice` row that references `packageId` when a package is known and a price exists for that package in the requested country/supplier/currency/effective date.

Precedence rules (lookup algorithm):
1. If a `packageId` is provided, search for `MaterialPrice` rows explicitly referencing that `packageId` (exact match). Use normal price selection rules (country/supplier/currency/effective date).
2. If no package-specific price exists, fall back to a product-level price (legacy behavior): search `MaterialPrice` rows that reference the `productId` or the legacy `materialId` for the formulation.
3. If multiple matches exist, apply standard tie-breaking (supplier preference, lowest cost if enabled, effective date latest-first). See Section 4.

Important constraints (must be enforced):
- Do NOT use `supplierReference` as a hidden package key. Keep mapping explicit and documented.
- Do NOT recommend storing composite ids in the existing `materialId` column. Avoid brittle workarounds.

4. Multiple prices — selection rules
----------------------------------
When multiple `MaterialPrice` rows are available, use deterministic selection:

- Filter by requested `country` (exact), else allow country-specific fallbacks only if system policy permits.
- Filter by requested `supplier` if provided; otherwise consider all suppliers.
- Normalize currency if caller requests a specific currency; otherwise prefer the price in the desired currency or convert at runtime using a trusted FX source (note: FX not part of this spec).
- Use `effectiveFrom`/`effectiveTo` to filter prices valid at the target date; prefer the most recent effective date.
- If multiple active prices pass filters, present selection policies:
  - Default: choose the lowest price per package (supplier-agnostic) as the recommended option.
  - Alternative: respect supplier preference or contractual supplier selection if configured.

Record selection reasons so Devis are auditable (see Section 7).

5. Hybrid package selection
-------------------------

Flow (conceptual):
1. Input: requiredQuantity (in KG or L) and `productId`.
2. Query `PaintPackageCatalog.getByProductId(productId)` and filter by `countryCode` and `active`.
3. For each available `PaintPackage`, fetch zero-or-more `MaterialPrice` candidates using package-aware lookup (Section 3 & 4).
4. Generate feasible package combinations that cover `requiredQuantity` (practical generation: try reasonable combinations; cap search depth and number of combos to keep computation tractable).
5. For each combination compute:
   - suppliedQuantity = sum(packages * packageSize)
   - waste = suppliedQuantity - requiredQuantity
   - packageCount = sum(packages)
   - totalCost = sum(packageQty * packagePrice) if prices available for all packages; otherwise mark cost as UNAVAILABLE
6. Rank candidate solutions:
   - Primary: lowest `totalCost` (only among candidates with available costs)
   - Secondary: lowest `waste`
   - Tertiary: lowest `packageCount`
7. If no price data is available for any package, fallback ranking: lowest `packageCount` then lowest `waste`.
8. Present top N candidates (e.g., 3) to the user with full breakdown and allow user override.

Rules when prices are partially available:
- If at least one candidate has full price coverage (all packages priced), include cost-based ranking for those candidates. Candidates with missing package prices should be flagged and ranked under no-cost fallback.

6. Units
--------
- Preserve units explicitly: `L` and `KG` are distinct.
- `PaintCalculator` and Devis must operate using the unit reported by `PaintProduct`/`PaintPackage` (i.e., if coverage is M2_PER_KG compute KG requirements and match to KG packages). Never convert KG ↔ L without authoritative density.

7. Auditability (Devis record requirements)
-----------------------------------------
Each generated Devis must capture:
- selected package(s): packageId and quantity
- calculated quantities: requiredQuantity, suppliedQuantity, waste
- price source for each package: MaterialPrice id and its effectiveFrom/To and supplier
- selection policy used (MIN_TOTAL_COST | MIN_PACKAGE_COUNT | USER_SELECTED | HYBRID)
- timestamp and user/account that confirmed the selection
- currency used and conversion references if price currency conversion was performed

8. Android / API / Web compatibility
----------------------------------
Design considerations for exposure without breaking contracts:

- API: add new, optional, package-aware endpoints or query parameters (non-breaking):
  - Example: `GET /pricing/packages?productId=...&country=...&date=...` returns packages + price candidates. If the client does not call this endpoint, legacy product-level pricing continues to work.
  - Example: `POST /quote/compute` with payload { productId, requiredQuantity, unit, policyHint } returns candidate packages and prices. Keep old quote endpoints untouched for backward compatibility.

- Android/Web: clients should call the package-aware endpoints when they want SKU-level quotes. UIs must be able to render candidate lists, price unavailability badges, and allow user override.

- Backwards compatibility: do not change existing endpoints for product-level pricing. New endpoints are additive and optional.

9. Migration strategy (conceptual)
--------------------------------
No SQL or exact schema actions here; high-level migration plan:

Phase 0 — In-memory / import layer
- Introduce `PaintPackage` in application layer (in-memory or config) and a package → price import process that writes package-aware price rows into `MaterialPrice` using a new `packageId` column (future). Until `packageId` exists in DB, keep mapping transient in importer.

Phase 1 — Add `PaintPackage` table (DB) and `packageId` column to `MaterialPrice` (nullable)
- Populate `PaintPackage` table from import/manual data.
- For each legacy `MaterialPrice` that was previously used for packages, either link to `packageId` if mapping exists or leave `packageId=null` for product-level prices.

Phase 2 — Migrate frontends and services to prefer `packageId` lookups
- Introduce package-aware API endpoints and update ingest pipelines.

Phase 3 — Cleanup
- After cutover and validation, deprecate ambiguous legacy fields and remove temporary import helpers.

10. Important rule (reiterated)
-----------------------------
PaintProduct = technical formulation
PaintPackage = package / SKU
MaterialPrice = price

Never mix responsibilities. Keep package ↔ price mapping explicit, auditable, and discoverable.

Appendix: Example request/response (conceptual)
---------------------------------------------

Request (compute candidates):
```
POST /quote/compute
{
  "productId": "ASTRAL_AQUASTRAL_SATIN",
  "requiredQuantity": 27.0,
  "unit": "KG",
  "country": "TN",
  "policyHint": "HYBRID"
}
```

Response (conceptual):
```
{
  "candidates": [
    {
      "packages": [{"packageId":"ASTRAL_AQUASTRAL_SATIN_20KG","qty":2}],
      "suppliedQuantity": 40,
      "waste": 13,
      "totalCost": 2 * 120.0,
      "costAvailable": true
    },
    {
      "packages": [{"packageId":"ASTRAL_AQUASTRAL_SATIN_20KG","qty":1},{"packageId":"ASTRAL_AQUASTRAL_SATIN_5KG","qty":2}],
      "suppliedQuantity": 30,
      "waste": 3,
      "totalCost": 1*120.0 + 2*35.0,
      "costAvailable": true
    }
  ],
  "notes": "Costs shown only when full package pricing is available."
}
```

Contact / Ownership
-------------------
This design doc is owned by the product/engineering team. Any change to `MaterialPrice` schema or pricing selection policies must be reviewed with Finance and Ops to ensure fiscal tracking and contract compliance.

---
Document created for KONSTRIVO paint package → price mapping planning. No code or DB changes performed.
