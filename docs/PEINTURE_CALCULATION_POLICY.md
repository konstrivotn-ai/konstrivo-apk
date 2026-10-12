# PEINTURE Calculation Policy (Verified Behavior)

This document records the current, verified behavior of the Peinture (painting)
calculations in the codebase. It is intentionally descriptive and does NOT
propose any automatic changes.

---

## 1. PEINTURE_INTERIEURE (Current production behavior)

- Primer (fixateur): 0.15 L/m²
- Primer container: 20 L (containers are computed as `ceil((area * 0.15) / 20.0)`).
- Finish (finitions): 0.25 L/m² (this value is the *total* consumption used in
  production calculations; see unresolved decision below).
- Finish container: 15 L (containers are computed as `ceil((area * 0.25) / 15.0)`)
- PaintCalculator integration: finish quantity calculation is routed through
  `PaintCalculator.calculatePaintLiters(...)` using `coverageM2PerLiter = 1.0 / 0.25 = 4.0`,
  `coats = 1`, `surfaceMultiplier = 1.0`, `wastePercent = 0.0`. The computed liters
  are then converted to 15L cans via `ceil(liters / 15.0)` to preserve legacy
  numeric results.
- Current effective coats used by production integration: 1 (the integration
  was parameterized with `coats = 1` to preserve legacy numbers).
- Waste currently applied to this branch: 0% (the finish calculation does not
  apply `params.wasteMarginPercent` in production; primer and finish formulas
  use hardcoded values and do not include waste multipliers).

## 2. PEINTURE_SABLE (Current production behavior)

- Primer (fixateur): 0.15 L/m² (same as interior primer)
- Primer container: 20 L
- Decorative sand paint consumption: 0.25 kg/m²
- Decorative sand container: 5 kg (containers computed as `ceil((area * 0.25) / 5.0)`).
- This branch was NOT modified during the recent refactor and remains using
  hardcoded consumption values.

## 3. PaintCalculator (helper function)

- Formula implemented in `PaintCalculator.calculatePaintLiters(...)`:

  netArea = max(0.0, area - openingsArea + additionalArea)

  liters = (netArea / coverageM2PerLiter)
           × coats
           × surfaceMultiplier
           × (1 + wastePercent / 100)

- Validation: function performs input guards and throws `IllegalArgumentException`
  for invalid required inputs (coverage <= 0, coats <= 0, negative netArea,
  wastePercent <= -100). The function is pure and deterministic.
- Tests: `PaintCalculatorTest.kt` contains unit tests asserting nominal result
  (example: 50m² -> 10.89 L with provided parameters) and validation checks
  (coverage=0 throws, coats=0 throws, negative openings handled, zero area -> 0 L).

## 4. IMPORTANT UNRESOLVED DECISION

The code uses `0.25 L/m²` for the finish. It is not encoded whether that value
represents:

- A) The *total* consumption for the full finish scope (for example, already
  representing two coats combined), or
- B) The consumption for a *single coat* (in which case two coats should be
  modeled as `coats = 2` and/or the numeric constant should change).

This decision remains unresolved and must NOT be made automatically by the
system. Choosing A vs B affects whether the finish should be parameterized as
`coats = 1` with the existing 0.25 L/m², or `coats = 2` with an adjusted
per-coat consumption.

## 5. Future target model (from engineering recommendations)

When the decision above is resolved, the intended (target) model for paint
calculations is:

- net area: `netArea = max(0, area - openings + additional)`
- openings: area to subtract (doors, windows) from total area
- coats: integer > 0 (number of finish coats to apply)
- coverage: `coverageM2PerLiter` (m² that 1 liter covers) — explicit, not
  implied
- surfaceMultiplier: surface condition multiplier (e.g., 1.0 default, >1 for
  rough surfaces)
- waste: percentage applied as multiplier `(1 + wastePercent / 100)` (must be
  bounded, e.g. > -100)

This model maps directly to `PaintCalculator.calculatePaintLiters(...)`.

## 6. State summary: current production vs future intended behavior

- Current production behavior: numeric values are hardcoded in
  `ConstructionCalculator` for primer, finish (0.25 L/m²), sand (kg/m²), and
  container sizes. Finish is computed via `PaintCalculator` but parameterized to
  reproduce legacy numbers (`coats = 1`, `waste = 0`).
- Future intended behavior: use `PaintCalculator` fully as the single source of
  truth for paint quantities, with explicit `coverageM2PerLiter`, `coats`,
  `surfaceMultiplier`, and `wastePercent` coming from input parameters or
  configurable material metadata.

## What must be decided before changing production behavior

1. Decide whether `0.25 L/m²` is total consumption (A) or per-coat consumption (B).
2. If B (per-coat), define the intended number of coats for each product
   (e.g., 2 coats) and whether `wasteMarginPercent` should apply to paint
   (and whether it should be the same `wasteMarginPercent` used for PLACO).
3. Decide where coverage metadata should live (material catalog, database,
   or constants) and the authoritative source for container sizes and unit
   economics.

---

Document created to capture the verified behavior as of this checkpoint.

---

## FREEZE: Current Production Rule (Do Not Change)

- PEINTURE_INTERIEURE finish consumption is FROZEN at **0.25 L/m²** as the
  total consumption for the recommended 2-coat finish system.
- Runtime code and unit tests currently use this value for calculating finish
  liters and 15L cans. This rule must not be changed in code, tests, pricing,
  database, API, or UI until an authoritative vendor datasheet or engineering
  source validates a replacement value.
- Note: `MaterialCatalog` contains a conflicting `technicalSpec` indicating
  per-coat coverage ~10–12 m²/L (2 coats recommended), which implies a
  different numeric total (≈0.166–0.20 L/m²). That discrepancy remains
  **UNRESOLVED** and must be reconciled with external documentation before
  making any numeric change.
