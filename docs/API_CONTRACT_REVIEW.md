# KONSTRIVO PRO — API Contract Review & Cross-Platform Alignment Report

**Date**: 2026-08-20  
**Phase**: Phase 8 (Next.js TypeScript Foundation & Contract Review)  
**Contract Files Reviewed**: `docs/API_CONTRACT.md` & `docs/openapi.yaml`  
**Target Platforms**: Android (Kotlin/Jetpack Compose), Web Portal (Next.js/TypeScript), Future Cloud API (PostgreSQL/Node.js/Go)

---

## 1. Executive Summary & Inventory

| Metric | API Contract Specification (`API_CONTRACT.md`) | OpenAPI Specification (`openapi.yaml`) | Alignment Status |
| :--- | :--- | :--- | :--- |
| **Distinct Path Routes** | 18 paths | 18 paths | ✅ 100% Match |
| **Total HTTP Operations** | 30 operations (including GET/POST/PATCH/DELETE variants) | 30 operations | ✅ 100% Match |
| **API Version** | `/api/v1` | `/api/v1` | ✅ 100% Match |
| **Data Types / Schemas** | 17 entities | 17 components/schemas | ✅ 100% Match |

---

## 2. Operation-by-Operation Audit

| # | Route | HTTP Verb | Category | Domain Status |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `/auth/login` | `POST` | Authentication | Future Contract |
| 2 | `/auth/refresh` | `POST` | Authentication | Future Contract |
| 3 | `/auth/logout` | `POST` | Authentication | Future Contract |
| 4 | `/auth/me` | `GET` | Authentication | Future Contract |
| 5 | `/users/me` | `GET` | Users & Settings | Future Contract |
| 6 | `/companies` | `POST` | Companies | Future Contract |
| 7 | `/companies/{companyId}` | `GET` | Companies | Future Contract |
| 8 | `/companies/{companyId}` | `PATCH` | Companies | Future Contract |
| 9 | `/trades` | `GET` | Trades & WorkTypes | Implemented in Domain |
| 10 | `/trades/{tradeId}` | `GET` | Trades & WorkTypes | Implemented in Domain |
| 11 | `/trades/{tradeId}/work-types` | `GET` | Trades & WorkTypes | Implemented in Domain |
| 12 | `/materials` | `GET` | Materials Catalog | Implemented in Domain |
| 13 | `/materials/{materialId}` | `GET` | Materials Catalog | Implemented in Domain |
| 14 | `/materials/{materialId}/prices` | `GET` | Pricing Catalog | Implemented in Domain |
| 15 | `/prices` | `GET` | Pricing Catalog | Implemented in Domain |
| 16 | `/markets` | `GET` | Markets & Currencies | Implemented in Domain |
| 17 | `/countries` | `GET` | Markets & Currencies | Implemented in Domain |
| 18 | `/currencies` | `GET` | Markets & Currencies | Implemented in Domain |
| 19 | `/projects` | `GET` | Projects Container | Future Contract |
| 20 | `/projects` | `POST` | Projects Container | Future Contract |
| 21 | `/projects/{projectId}` | `GET` | Projects Container | Future Contract |
| 22 | `/projects/{projectId}` | `PATCH` | Projects Container | Future Contract |
| 23 | `/projects/{projectId}` | `DELETE` | Projects Container | Future Contract |
| 24 | `/estimates` | `GET` | Estimates & Quotes | Implemented in Domain |
| 25 | `/estimates` | `POST` | Estimates & Quotes | Implemented in Domain |
| 26 | `/estimates/{estimateId}` | `GET` | Estimates & Quotes | Implemented in Domain |
| 27 | `/estimates/{estimateId}` | `PATCH` | Estimates & Quotes | Implemented in Domain |
| 28 | `/estimates/{estimateId}` | `DELETE` | Estimates & Quotes | Implemented in Domain |
| 29 | `/calculations` | `POST` | Calculation Engine | Cloud Mirror (Offline in Android) |
| 30 | `/sync/delta` | `POST` | Sync Engine | Future Contract |

---

## 3. Discovered Inconsistencies & Alignment Notes

### 3.1 Market Context Example Breadth
- **Observation**: `docs/API_CONTRACT.md` illustrated an example JSON containing 4 countries (`TN`, `FR`, `DZ`, `MA`), whereas the Android domain model `MarketContext.kt` defines 9 active markets (`TN`, `FR`, `DZ`, `MA`, `CA`, `SA`, `AE`, `EG`, `LY`).
- **Resolution for Web/Next.js**: The TypeScript market configuration (`src/config/markets.ts`) implements the full set of 9 ISO market profiles to match the Android domain model while remaining strictly typed to the OpenAPI schema.

### 3.2 Material vs. Price Decoupling
- **Observation**: Verified that in both `API_CONTRACT.md` and `openapi.yaml`, `Material` schema has **zero pricing fields**, and `MaterialPrice` references `materialId` and contains `defaultPrice` and `currentPrice` typed as `Money` (`{ amount, currency }`).
- **Resolution**: Fully consistent across domain models, OpenAPI specifications, and TypeScript typings.

### 3.3 Offline vs. Online Calculations
- **Observation**: Endpoint `POST /api/v1/calculations` exists in the contract for server-side/Web usage, but Android continues to execute calculations 100% offline via local Kotlin calculators (`ConstructionCalculator.kt` and `PeintureCalculator.kt`).
- **Resolution**: Clear architectural boundary documented: Web frontend can compute estimates either client-side or via the API gateway without altering the Android client's offline autonomy.

---

## 4. Cross-Platform Type Compatibility Verification
- **Android Kotlin**: Data classes in `com.example.placo.domain.model`
- **TypeScript**: Shared types in `src/types/api/` and generated types in `src/types/generated/`
- **Validation**:
  - All IDs are typed as strict alphanumeric identifiers (`placo`, `plaque_ba13_standard`, `TN_plaque_ba13_standard`).
  - No localized strings are used as enum values or entity IDs.
  - Timestamps are strictly ISO 8601 UTC strings (`string & { readonly __brand: 'ISODateTime' }` or standard `string`).
  - Money objects (`{ amount: number; currency: string }`) are strictly enforced.
