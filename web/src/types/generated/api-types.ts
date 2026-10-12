/**
 * This file is generated / structured strictly from docs/openapi.yaml.
 * Do NOT modify manually unless updating the OpenAPI schema.
 */

export interface Money {
  amount: number;
  currency: string;
}

export interface PaginationMeta {
  page: number;
  limit: number;
  totalItems: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPreviousPage: boolean;
}

export interface ApiError {
  error: {
    code: string;
    message: string;
    details?: Record<string, unknown>;
    requestId: string;
    timestamp: string;
  };
}

export interface LoginRequest {
  provider: 'password' | 'google_oauth2';
  identifier: string;
  secret: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserClaims {
  id: string;
  email: string;
  phone?: string;
  fullName: string;
  companyId?: string;
  roles: string[];
  permissions: string[];
}

export type SupportedLanguage = 'FRENCH' | 'ARABIC' | 'DERJA' | 'ENGLISH';

export interface UserProfile {
  id: string;
  fullName: string;
  email?: string;
  phone?: string;
  preferredLanguage: SupportedLanguage;
  defaultCountryCode: string;
  defaultWasteMarginPercent?: number;
  defaultLaborRate?: Money;
  companyId?: string;
}

export interface Company {
  id?: string;
  name: string;
  taxId?: string;
  phone?: string;
  address?: string;
  city?: string;
  countryCode: string;
  currency: string;
  logoUrl?: string;
}

export type TradeId =
  | 'placo'
  | 'peinture'
  | 'maconnerie'
  | 'crepissage'
  | 'carrelage'
  | 'menuiserie';

export interface Trade {
  id: TradeId;
  localizationKey: string;
  iconEmoji: string;
  workTypeCount?: number;
  supportedUnitSystems?: string[];
}

export interface WorkType {
  id: string;
  localizationKey: string;
  defaultUnit: string;
  requiredInputs: string[];
  optionalInputs?: string[];
}

export type MaterialCategory =
  | 'placo'
  | 'ossature'
  | 'visserie'
  | 'enduits'
  | 'dalles'
  | 'isolation'
  | 'peinture'
  | 'maconnerie'
  | 'crepissage'
  | 'carrelage'
  | 'bois_alu'
  | 'plomberie'
  | 'electricite'
  | 'etancheite'
  | 'outillage'
  | 'autre';

export type StandardUnit =
  | 'piece'
  | 'm2'
  | 'ml'
  | 'm3'
  | 'kg'
  | 'g'
  | 'liter'
  | 'bag_25kg'
  | 'bag_50kg'
  | 'bag_5kg'
  | 'box_1000'
  | 'box_500'
  | 'box_200'
  | 'box_7_2m2'
  | 'bucket_15l'
  | 'bucket_20l'
  | 'bucket_5kg'
  | 'roll_90m'
  | 'roll_50m'
  | 'roll_30m'
  | 'roll_15m2'
  | 'roll_10m'
  | 'cartridge'
  | 'aerosol_750ml'
  | 'custom';

export interface Material {
  id: string;
  category: MaterialCategory;
  standardUnit: StandardUnit;
  localizationKey: string;
  brandInfo?: string;
  technicalSpec?: string;
  compatibleTrades: TradeId[] | string[];
  isActive: boolean;
}

export type PriceSource =
  | 'official_btp'
  | 'market_2026'
  | 'supplier'
  | 'contractor_custom'
  | 'imported'
  | 'api_sync'
  | 'user_override';

export interface MaterialPrice {
  id: string;
  materialId: string;
  countryCode: string;
  region?: string;
  unit: string;
  defaultPrice: Money;
  currentPrice: Money;
  priceSource: PriceSource;
  supplierName?: string;
  effectiveFrom: string;
  effectiveTo?: string | null;
  observedAt: string;
  status: 'ACTIVE' | 'ARCHIVED';
}

export interface MarketContext {
  countryCode: string;
  countryName: string;
  currencyCode: string;
  currencySymbol: string;
  defaultLaborRatePerM2: Money;
  defaultWasteMarginPercent: number;
  unitSystem: string;
  hasActiveMarketData: boolean;
}

export interface Project {
  id?: string;
  companyId?: string;
  title: string;
  clientName?: string;
  clientPhone?: string;
  location?: string;
  status: 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED' | 'ARCHIVED';
  estimateCount?: number;
  createdAt: string;
  updatedAt?: string;
}

export interface EstimateItem {
  materialId: string;
  quantity: number;
  unit: string;
  unitPrice: Money;
  totalPrice: Money;
}

export interface Estimate {
  id?: string;
  projectId?: string | null;
  clientName: string;
  clientPhone?: string;
  tradeId: TradeId | string;
  workTypeId: string;
  countryCode: string;
  currency: string;
  dimensions: {
    area: number;
    perimeter: number;
    wasteMarginPercent?: number;
  };
  items: EstimateItem[];
  financialSummary: {
    materialCost: Money;
    laborCost: Money;
    taxAmount?: Money;
    grandTotal: Money;
  };
  status: 'DRAFT' | 'SENT' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  updatedAt?: string;
}

export interface CalculationRequest {
  tradeId: TradeId | string;
  workTypeId: string;
  countryCode: string;
  inputs: {
    area: number;
    perimeter: number;
    wasteMarginPercent?: number;
    customLaborRate?: number;
    [key: string]: unknown;
  };
}

export interface CalculatedMaterialItem {
  materialId: string;
  quantity: number;
  unit: string;
  unitPrice: Money;
  totalPrice: Money;
  wasteFactorApplied: number;
}

export interface CalculationResponse {
  tradeId: string;
  workTypeId: string;
  countryCode: string;
  currency: string;
  inputs: Record<string, unknown>;
  materials: CalculatedMaterialItem[];
  totals: {
    materialCost: Money;
    laborCost: Money;
    grandTotal: Money;
  };
}

export interface SyncMutation {
  entityType: 'PRICE_OVERRIDE' | 'ESTIMATE' | 'USER_SETTINGS' | 'PROJECT';
  entityId: string;
  action: 'CREATE' | 'UPSERT' | 'DELETE';
  clientUpdatedAt: string;
  version: number;
  payload: Record<string, unknown>;
}

export interface SyncDeltaRequest {
  clientDeviceId: string;
  lastSyncedTimestamp: string;
  mutations: SyncMutation[];
}

export interface SyncDeltaResponse {
  serverTimestamp: string;
  appliedCount: number;
  conflicts: unknown[];
  serverChanges: unknown[];
}
