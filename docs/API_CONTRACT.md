# KONSTRIVO PRO — API Contract Specification

**API Version**: `v1`  
**Base URL**: `/api/v1`  
**Protocol**: HTTPS  
**Content-Type**: `application/json; charset=utf-8`  
**Status**: Formal Architectural Contract (Phase 7) — Ready for Multi-Platform consumption (Android Kotlin, Web Next.js/TypeScript, and Future Backend/PostgreSQL Services).

---

## 1. Architectural Principles & Standard Conventions

### 1.1 Separation of Concerns
1. **Material Definition vs. Price Point**:
   - `Material` describes physical traits, packaging specs, standard measurement units, compatible trades, and classification. It contains **no price**.
   - `MaterialPrice` models geographic/market valuation, currency, supplier provenance, validity period, and observation timestamps.
2. **Offline-First & Local Independence**:
   - The Android client calculation engines (`ConstructionCalculator`, `PeintureCalculator`) operate 100% offline using local Room storage.
   - The API contract provides an optional synchronization and cloud-computation layer without requiring always-on internet connectivity.
3. **Multi-Currency & Multi-Market Design**:
   - Currencies and prices are explicitly separated from display formatting.
   - Raw amounts are strictly numerical in their native currency; localized symbols and exchange conversions are handled independently.

### 1.2 Unified Money Representation
Monetary amounts are never serialized as localized strings (e.g. `"1 250,500 DT"`). They strictly follow the `Money` object schema:
```json
{
  "amount": 1250.50,
  "currency": "TND"
}
```

### 1.3 Unified Date & Time Representation
All timestamps follow **ISO 8601 UTC** with `Z` notation:
```json
"2026-08-20T07:30:00.000Z"
```

### 1.4 Standardized Pagination Wrapper
Paginated list endpoints follow a consistent envelope:
```json
{
  "data": [ /* Array of Items */ ],
  "meta": {
    "page": 1,
    "limit": 20,
    "totalItems": 145,
    "totalPages": 8,
    "hasNextPage": true,
    "hasPreviousPage": false
  },
  "links": {
    "self": "/api/v1/materials?page=1&limit=20",
    "next": "/api/v1/materials?page=2&limit=20",
    "prev": null
  }
}
```

### 1.5 Standardized Error Contract
All error responses adhere to RFC 7807 problem details semantics:
```json
{
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Input validation error occurred on the provided parameters.",
    "details": {
      "area": "The area must be greater than 0.0"
    },
    "requestId": "req_8f1a23c09b",
    "timestamp": "2026-08-20T07:30:00.000Z"
  }
}
```
**Standard Error Codes**:
- `UNAUTHORIZED` (401): Missing or expired bearer token.
- `FORBIDDEN` (403): User does not possess sufficient organizational permissions.
- `NOT_FOUND` (404): Resource ID does not exist.
- `CONFLICT` (409): State conflict or optimistic lock failure during update.
- `VALIDATION_FAILED` (422): Malformed payload fields.
- `RATE_LIMITED` (429): Request quota exceeded.
- `INTERNAL_SERVER_ERROR` (500): Unhandled backend server fault.

---

## 2. Authentication & Session Management *(Future Contract)*

### 2.1 `POST /api/v1/auth/login`
- **Description**: Authenticate with email/phone credentials or Google OAuth2 tokens.
- **Auth Required**: No
- **Request Body**:
```json
{
  "provider": "password",
  "identifier": "artisan@konstrivo.tn",
  "secret": "securePassword123"
}
```
- **Response `200 OK`**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
  "refreshToken": "dGhpcy1pcy1hLXJlZnJlc2gtdG9rZW4...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": "usr_99182374",
    "email": "artisan@konstrivo.tn",
    "fullName": "Mohamed Amine",
    "companyId": "cmp_102938"
  }
}
```

### 2.2 `POST /api/v1/auth/refresh`
- **Description**: Exchange a valid refresh token for a fresh access token.
- **Auth Required**: No
- **Request Body**:
```json
{
  "refreshToken": "dGhpcy1pcy1hLXJlZnJlc2gtdG9rZW4..."
}
```
- **Response `200 OK`**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
  "refreshToken": "dGhpcy1pcy1uZXctcmVmcmVzaC10b2tlbg...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 2.3 `POST /api/v1/auth/logout`
- **Description**: Invalidate active session and refresh token.
- **Auth Required**: Yes (Bearer)
- **Response `204 No Content`**

### 2.4 `GET /api/v1/auth/me`
- **Description**: Retrieve active identity profile and tenant claims.
- **Auth Required**: Yes (Bearer)
- **Response `200 OK`**:
```json
{
  "id": "usr_99182374",
  "email": "artisan@konstrivo.tn",
  "phone": "+21698123456",
  "fullName": "Mohamed Amine",
  "companyId": "cmp_102938",
  "roles": ["CONTRACTOR_ADMIN"],
  "permissions": ["estimates:write", "prices:override", "projects:manage"]
}
```

---

## 3. Users, Companies & Roles *(Future Contract)*

### 3.1 `GET /api/v1/users/me`
- **Description**: Detailed user preferences, preferred language, and active country context.
- **Auth Required**: Yes (Bearer)
- **Response `200 OK`**:
```json
{
  "id": "usr_99182374",
  "fullName": "Mohamed Amine",
  "email": "artisan@konstrivo.tn",
  "phone": "+21698123456",
  "preferredLanguage": "DERJA",
  "defaultCountryCode": "TN",
  "defaultWasteMarginPercent": 5.0,
  "defaultLaborRate": {
    "amount": 12.0,
    "currency": "TND"
  },
  "companyId": "cmp_102938",
  "createdAt": "2026-01-10T12:00:00.000Z"
}
```

### 3.2 `POST /api/v1/companies`
- **Description**: Create a contractor enterprise or quincaillerie store profile.
- **Auth Required**: Yes (Bearer)
- **Request Body**:
```json
{
  "name": "Entreprise Placo & Finition Tunisie",
  "taxId": "1234567/A/M/000",
  "phone": "+21671234567",
  "address": "Zone Industrielle Ariana",
  "city": "Ariana",
  "countryCode": "TN",
  "currency": "TND",
  "logoUrl": "https://cdn.konstrivo.tn/logos/cmp_102938.png"
}
```

### 3.3 `GET /api/v1/companies/{companyId}`
- **Description**: Retrieve company profile by ID.
- **Auth Required**: Yes (Bearer)

### 3.4 `PATCH /api/v1/companies/{companyId}`
- **Description**: Update company profile metadata.
- **Auth Required**: Yes (Bearer)

---

## 4. Trades & Work Types Catalog *(Domain Aligned)*

### 4.1 `GET /api/v1/trades`
- **Description**: Fetch all standardized construction trades.
- **Auth Required**: Optional
- **Response `200 OK`**:
```json
{
  "trades": [
    {
      "id": "placo",
      "localizationKey": "trade_placo",
      "iconEmoji": "🏗️",
      "workTypeCount": 7,
      "supportedUnitSystems": ["METRIC"]
    },
    {
      "id": "peinture",
      "localizationKey": "trade_peinture",
      "iconEmoji": "🎨",
      "workTypeCount": 2,
      "supportedUnitSystems": ["METRIC"]
    },
    {
      "id": "maconnerie",
      "localizationKey": "trade_maconnerie",
      "iconEmoji": "🧱",
      "workTypeCount": 1,
      "supportedUnitSystems": ["METRIC"]
    },
    {
      "id": "crepissage",
      "localizationKey": "trade_crepissage",
      "iconEmoji": "🖌️",
      "workTypeCount": 1,
      "supportedUnitSystems": ["METRIC"]
    },
    {
      "id": "carrelage",
      "localizationKey": "trade_carrelage",
      "iconEmoji": "🟦",
      "workTypeCount": 1,
      "supportedUnitSystems": ["METRIC"]
    },
    {
      "id": "menuiserie",
      "localizationKey": "trade_menuiserie",
      "iconEmoji": "🪟",
      "workTypeCount": 1,
      "supportedUnitSystems": ["METRIC"]
    }
  ]
}
```

### 4.2 `GET /api/v1/trades/{tradeId}`
- **Description**: Fetch detailed trade metadata.

### 4.3 `GET /api/v1/trades/{tradeId}/work-types`
- **Description**: Fetch all calculation presets and work types under a trade.
- **Example for `tradeId=placo`**:
```json
{
  "tradeId": "placo",
  "workTypes": [
    {
      "id": "placo_plafond_simple",
      "localizationKey": "calc_faux_plafond_simple",
      "defaultUnit": "m2",
      "requiredInputs": ["area", "perimeter"],
      "optionalInputs": ["wasteMarginPercent", "customLaborRate"]
    },
    {
      "id": "placo_cloison_separation",
      "localizationKey": "calc_cloison_separation",
      "defaultUnit": "m2",
      "requiredInputs": ["area", "perimeter"],
      "optionalInputs": ["isDoubleSkin", "insulationType"]
    },
    {
      "id": "placo_plafond_demontable",
      "localizationKey": "calc_plafond_demontable",
      "defaultUnit": "m2",
      "requiredInputs": ["area", "perimeter"],
      "optionalInputs": ["tileType"]
    },
    {
      "id": "placo_habillage_exterieur",
      "localizationKey": "calc_habillage_aquapanel",
      "defaultUnit": "m2",
      "requiredInputs": ["area", "perimeter"],
      "optionalInputs": []
    }
  ]
}
```

---

## 5. Materials Catalog *(Domain Aligned)*

### 5.1 `GET /api/v1/materials`
- **Description**: List construction materials with filtering by category or trade.
- **Query Parameters**:
  - `category` (optional, e.g. `placo`, `ossature`, `peinture`, `enduits`, `visserie`)
  - `trade` (optional, e.g. `placo`, `peinture`, `maconnerie`)
  - `search` (optional)
  - `page`, `limit`
- **Response `200 OK`**:
```json
{
  "data": [
    {
      "id": "plaque_ba13_standard",
      "category": "placo",
      "standardUnit": "piece",
      "localizationKey": "mat_plaque_ba13_standard",
      "brandInfo": "Knauf / Siniat / Fassa",
      "technicalSpec": "Épaisseur 12.5mm • Largeur 1.2m • Longueur 2.5m • Poids ~8.5 kg/m²",
      "compatibleTrades": ["placo"],
      "isActive": true
    },
    {
      "id": "fourrure",
      "category": "ossature",
      "standardUnit": "piece",
      "localizationKey": "mat_fourrure",
      "brandInfo": "Perfilkit / Knauf",
      "technicalSpec": "Profilé porteur plafond suspendu F47 (3m)",
      "compatibleTrades": ["placo"],
      "isActive": true
    },
    {
      "id": "enduit_lissage_peinture_25kg",
      "category": "peinture",
      "standardUnit": "bag_25kg",
      "localizationKey": "mat_enduit_lissage_25kg",
      "brandInfo": "Astral / Sovap / Fassa",
      "technicalSpec": "Rendement moyen ~1.2 kg/m² pour 2 passes de ratissage",
      "compatibleTrades": ["peinture"],
      "isActive": true
    }
  ],
  "meta": {
    "page": 1,
    "limit": 20,
    "totalItems": 45,
    "totalPages": 3
  }
}
```

### 5.2 `GET /api/v1/materials/{materialId}`
- **Description**: Retrieve specific material physical traits. Contains **no pricing information**.

---

## 6. Prices & Market Tariffs *(Domain Aligned)*

### 6.1 `GET /api/v1/prices`
- **Description**: Query market prices by country, region, and source.
- **Query Parameters**:
  - `countryCode` (required, e.g. `TN`, `FR`, `DZ`, `MA`)
  - `region` (optional, e.g. `Grand Tunis`, `Sousse`, `Paris`)
  - `source` (optional, e.g. `market_2026`, `official_btp`, `supplier`)
- **Response `200 OK`**:
```json
{
  "countryCode": "TN",
  "currency": "TND",
  "data": [
    {
      "id": "TN_plaque_ba13_standard",
      "materialId": "plaque_ba13_standard",
      "countryCode": "TN",
      "region": "Grand Tunis",
      "unit": "piece",
      "defaultPrice": {
        "amount": 30.00,
        "currency": "TND"
      },
      "currentPrice": {
        "amount": 30.00,
        "currency": "TND"
      },
      "priceSource": "market_2026",
      "supplierName": "Moyenne Marché 2026",
      "effectiveFrom": "2026-01-01T00:00:00.000Z",
      "effectiveTo": null,
      "observedAt": "2026-08-20T00:00:00.000Z",
      "status": "ACTIVE"
    },
    {
      "id": "TN_enduit_lissage_peinture_25kg",
      "materialId": "enduit_lissage_peinture_25kg",
      "countryCode": "TN",
      "region": "Grand Tunis",
      "unit": "bag_25kg",
      "defaultPrice": {
        "amount": 35.00,
        "currency": "TND"
      },
      "currentPrice": {
        "amount": 35.00,
        "currency": "TND"
      },
      "priceSource": "market_2026",
      "supplierName": "Moyenne Marché 2026",
      "effectiveFrom": "2026-01-01T00:00:00.000Z",
      "effectiveTo": null,
      "observedAt": "2026-08-20T00:00:00.000Z",
      "status": "ACTIVE"
    }
  ]
}
```

### 6.2 `GET /api/v1/materials/{materialId}/prices`
- **Description**: Fetch all regional and international price points for a single material.

---

## 7. Markets, Countries & Currencies *(Domain Aligned)*

### 7.1 `GET /api/v1/markets`
- **Description**: Fetch all supported geographic markets, currency configurations, and baseline labor rates.
- **Response `200 OK`**:
```json
{
  "markets": [
    {
      "countryCode": "TN",
      "countryName": "Tunisie",
      "currencyCode": "TND",
      "currencySymbol": "د.ت",
      "defaultLaborRatePerM2": {
        "amount": 12.00,
        "currency": "TND"
      },
      "defaultWasteMarginPercent": 5.0,
      "unitSystem": "METRIC",
      "hasActiveMarketData": true
    },
    {
      "countryCode": "FR",
      "countryName": "France",
      "currencyCode": "EUR",
      "currencySymbol": "€",
      "defaultLaborRatePerM2": {
        "amount": 28.00,
        "currency": "EUR"
      },
      "defaultWasteMarginPercent": 5.0,
      "unitSystem": "METRIC",
      "hasActiveMarketData": true
    },
    {
      "countryCode": "DZ",
      "countryName": "Algérie",
      "currencyCode": "DZD",
      "currencySymbol": "د.ج",
      "defaultLaborRatePerM2": {
        "amount": 500.00,
        "currency": "DZD"
      },
      "defaultWasteMarginPercent": 5.0,
      "unitSystem": "METRIC",
      "hasActiveMarketData": true
    },
    {
      "countryCode": "MA",
      "countryName": "Maroc",
      "currencyCode": "MAD",
      "currencySymbol": "د.م",
      "defaultLaborRatePerM2": {
        "amount": 40.00,
        "currency": "MAD"
      },
      "defaultWasteMarginPercent": 5.0,
      "unitSystem": "METRIC",
      "hasActiveMarketData": true
    }
  ]
}
```

### 7.2 `GET /api/v1/countries`
- **Description**: List of all supported ISO-3166-1 alpha-2 country profiles.

### 7.3 `GET /api/v1/currencies`
- **Description**: List of supported currencies and baseline exchange rates relative to base reference.

---

## 8. Projects & Sites *(Future Contract)*

### 8.1 `GET /api/v1/projects`
- **Description**: List active construction sites/projects for the authenticated user/company.
- **Auth Required**: Yes (Bearer)
- **Response `200 OK`**:
```json
{
  "data": [
    {
      "id": "prj_2026_001",
      "companyId": "cmp_102938",
      "title": "Villa Ennasr — Faux Plafonds & Peinture",
      "clientName": "Si Moncef Trabelsi",
      "clientPhone": "+21698223344",
      "location": "Ennasr 2, Ariana",
      "status": "IN_PROGRESS",
      "estimateCount": 2,
      "createdAt": "2026-08-15T09:00:00.000Z",
      "updatedAt": "2026-08-20T07:00:00.000Z"
    }
  ]
}
```

### 8.2 `POST /api/v1/projects`
- **Description**: Create a new project site container.
- **Auth Required**: Yes (Bearer)

### 8.3 `GET /api/v1/projects/{projectId}`
### 8.4 `PATCH /api/v1/projects/{projectId}`
### 8.5 `DELETE /api/v1/projects/{projectId}`

---

## 9. Estimates & Quotes (Devis) *(Domain Aligned)*

### 9.1 `GET /api/v1/estimates`
- **Description**: Retrieve saved estimates for a company or project.
- **Auth Required**: Yes (Bearer)
- **Response `200 OK`**:
```json
{
  "data": [
    {
      "id": "est_2026_0981",
      "projectId": "prj_2026_001",
      "clientName": "Si Moncef Trabelsi",
      "clientPhone": "+21698223344",
      "tradeId": "placo",
      "workTypeId": "placo_plafond_simple",
      "countryCode": "TN",
      "currency": "TND",
      "dimensions": {
        "area": 45.0,
        "perimeter": 28.0,
        "wasteMarginPercent": 5.0
      },
      "items": [
        {
          "materialId": "plaque_ba13_standard",
          "quantity": 16.0,
          "unit": "piece",
          "unitPrice": { "amount": 30.00, "currency": "TND" },
          "totalPrice": { "amount": 480.00, "currency": "TND" }
        },
        {
          "materialId": "fourrure",
          "quantity": 30.0,
          "unit": "piece",
          "unitPrice": { "amount": 7.00, "currency": "TND" },
          "totalPrice": { "amount": 210.00, "currency": "TND" }
        }
      ],
      "financialSummary": {
        "materialCost": { "amount": 845.00, "currency": "TND" },
        "laborCost": { "amount": 540.00, "currency": "TND" },
        "taxAmount": { "amount": 0.00, "currency": "TND" },
        "grandTotal": { "amount": 1385.00, "currency": "TND" }
      },
      "status": "APPROVED",
      "createdAt": "2026-08-20T07:15:00.000Z",
      "updatedAt": "2026-08-20T07:15:00.000Z"
    }
  ]
}
```

### 9.2 `POST /api/v1/estimates`
- **Description**: Save a newly generated estimate quote.
- **Auth Required**: Yes (Bearer)

### 9.3 `GET /api/v1/estimates/{estimateId}`
### 9.4 `PATCH /api/v1/estimates/{estimateId}`
### 9.5 `DELETE /api/v1/estimates/{estimateId}`

---

## 10. Calculations Engine API *(Future Cloud Contract)*

*Note: The Android application continues to calculate locally offline via `ConstructionCalculator.kt` and `CalculateConstructionUseCase.kt`. This endpoint provides identical calculation results on the cloud API for Web or backend-driven quotes.*

### 10.1 `POST /api/v1/calculations`
- **Description**: Compute material breakdown, consumptions, and labor for a given trade and dimensions.
- **Request Body**:
```json
{
  "tradeId": "placo",
  "workTypeId": "placo_plafond_simple",
  "countryCode": "TN",
  "inputs": {
    "area": 50.0,
    "perimeter": 30.0,
    "wasteMarginPercent": 5.0,
    "customLaborRate": 12.0
  }
}
```
- **Response `200 OK`**:
```json
{
  "tradeId": "placo",
  "workTypeId": "placo_plafond_simple",
  "countryCode": "TN",
  "currency": "TND",
  "inputs": {
    "area": 50.0,
    "perimeter": 30.0,
    "wasteMarginPercent": 5.0
  },
  "materials": [
    {
      "materialId": "plaque_ba13_standard",
      "quantity": 18.0,
      "unit": "piece",
      "unitPrice": { "amount": 30.00, "currency": "TND" },
      "totalPrice": { "amount": 540.00, "currency": "TND" },
      "wasteFactorApplied": 1.05
    },
    {
      "materialId": "fourrure",
      "quantity": 34.0,
      "unit": "piece",
      "unitPrice": { "amount": 7.00, "currency": "TND" },
      "totalPrice": { "amount": 238.00, "currency": "TND" },
      "wasteFactorApplied": 1.0
    },
    {
      "materialId": "corniere_angle",
      "quantity": 10.0,
      "unit": "piece",
      "unitPrice": { "amount": 6.50, "currency": "TND" },
      "totalPrice": { "amount": 65.00, "currency": "TND" },
      "wasteFactorApplied": 1.0
    },
    {
      "materialId": "suspente",
      "quantity": 60.0,
      "unit": "piece",
      "unitPrice": { "amount": 0.80, "currency": "TND" },
      "totalPrice": { "amount": 48.00, "currency": "TND" },
      "wasteFactorApplied": 1.0
    },
    {
      "materialId": "bande_a_joint_90m",
      "quantity": 1.0,
      "unit": "roll_90m",
      "unitPrice": { "amount": 18.00, "currency": "TND" },
      "totalPrice": { "amount": 18.00, "currency": "TND" },
      "wasteFactorApplied": 1.0
    },
    {
      "materialId": "enduit_joint_25kg",
      "quantity": 1.0,
      "unit": "bag_25kg",
      "unitPrice": { "amount": 42.00, "currency": "TND" },
      "totalPrice": { "amount": 42.00, "currency": "TND" },
      "wasteFactorApplied": 1.0
    },
    {
      "materialId": "vis_placo_25",
      "quantity": 1.0,
      "unit": "box_1000",
      "unitPrice": { "amount": 22.00, "currency": "TND" },
      "totalPrice": { "amount": 22.00, "currency": "TND" },
      "wasteFactorApplied": 1.0
    }
  ],
  "totals": {
    "materialCost": { "amount": 973.00, "currency": "TND" },
    "laborCost": { "amount": 600.00, "currency": "TND" },
    "grandTotal": { "amount": 1573.00, "currency": "TND" }
  }
}
```

---

## 11. Offline-First Cloud Synchronization Protocol *(Future Contract)*

To synchronize offline records from Room SQLite to PostgreSQL without merge loss:

### 11.1 Delta Sync Protocol: `POST /api/v1/sync/delta`
- **Request Body**:
```json
{
  "clientDeviceId": "dev_android_pixel_8a",
  "lastSyncedTimestamp": "2026-08-19T10:00:00.000Z",
  "mutations": [
    {
      "entityType": "PRICE_OVERRIDE",
      "entityId": "TN_plaque_ba13_standard",
      "action": "UPSERT",
      "clientUpdatedAt": "2026-08-20T07:10:00.000Z",
      "version": 3,
      "payload": {
        "currentPrice": 31.50
      }
    },
    {
      "entityType": "ESTIMATE",
      "entityId": "est_local_99182",
      "action": "CREATE",
      "clientUpdatedAt": "2026-08-20T07:15:00.000Z",
      "version": 1,
      "payload": { /* Full Estimate Body */ }
    }
  ]
}
```
- **Response `200 OK`**:
```json
{
  "serverTimestamp": "2026-08-20T07:35:00.000Z",
  "appliedCount": 2,
  "conflicts": [],
  "serverChanges": [
    {
      "entityType": "MARKET_PRICE",
      "entityId": "TN_ciment_portland_50kg",
      "action": "UPSERT",
      "serverUpdatedAt": "2026-08-20T06:00:00.000Z",
      "version": 5,
      "payload": {
        "currentPrice": 14.50
      }
    }
  ]
}
```

### 11.2 Tombstone Deletion Policy
- Hard deletions on local mobile devices must record a tombstone entity with `deletedAt: ISODate` to propagate the deletion safely to cloud storage and other paired devices.
