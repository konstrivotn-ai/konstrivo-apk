import { apiClient, ApiClient } from './client';
import {
  AuthResponse,
  CalculationRequest,
  CalculationResponse,
  Company,
  Estimate,
  LoginRequest,
  MarketContext,
  Material,
  MaterialPrice,
  Project,
  SyncDeltaRequest,
  SyncDeltaResponse,
  Trade,
  UserClaims,
  UserProfile,
  WorkType,
} from '@/types/api';

export class KonstrivoApi {
  constructor(private client: ApiClient = apiClient) {}

  // --- Auth & Users ---
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    return this.client.post<AuthResponse>('/auth/login', credentials);
  }

  async refreshToken(refreshToken: string): Promise<AuthResponse> {
    return this.client.post<AuthResponse>('/auth/refresh', { refreshToken });
  }

  async logout(token: string): Promise<void> {
    return this.client.post<void>('/auth/logout', undefined, { token });
  }

  async getAuthMe(token: string): Promise<UserClaims> {
    return this.client.get<UserClaims>('/auth/me', { token });
  }

  async getUserProfile(token: string): Promise<UserProfile> {
    return this.client.get<UserProfile>('/users/me', { token });
  }

  // --- Companies ---
  async getCompany(companyId: string, token: string): Promise<Company> {
    return this.client.get<Company>(`/companies/${companyId}`, { token });
  }

  async createCompany(company: Company, token: string): Promise<Company> {
    return this.client.post<Company>('/companies', company, { token });
  }

  async updateCompany(companyId: string, company: Partial<Company>, token: string): Promise<Company> {
    return this.client.patch<Company>(`/companies/${companyId}`, company, { token });
  }

  // --- Trades & Work Types ---
  async getTrades(): Promise<{ trades: Trade[] }> {
    return this.client.get<{ trades: Trade[] }>('/trades');
  }

  async getTrade(tradeId: string): Promise<Trade> {
    return this.client.get<Trade>(`/trades/${tradeId}`);
  }

  async getWorkTypes(tradeId: string): Promise<{ tradeId: string; workTypes: WorkType[] }> {
    return this.client.get<{ tradeId: string; workTypes: WorkType[] }>(`/trades/${tradeId}/work-types`);
  }

  // --- Materials & Prices ---
  async getMaterials(params?: { category?: string; trade?: string; search?: string; page?: number; limit?: number }) {
    return this.client.get<{ data: Material[]; meta: unknown }>('/materials', { params });
  }

  async getMaterial(materialId: string): Promise<Material> {
    return this.client.get<Material>(`/materials/${materialId}`);
  }

  async getMaterialPrices(materialId: string): Promise<{ materialId: string; prices: MaterialPrice[] }> {
    return this.client.get<{ materialId: string; prices: MaterialPrice[] }>(`/materials/${materialId}/prices`);
  }

  async getMarketPrices(countryCode: string, region?: string): Promise<{ countryCode: string; currency: string; data: MaterialPrice[] }> {
    return this.client.get<{ countryCode: string; currency: string; data: MaterialPrice[] }>('/prices', {
      params: { countryCode, region },
    });
  }

  // --- Markets & Geography ---
  async getMarkets(): Promise<{ markets: MarketContext[] }> {
    return this.client.get<{ markets: MarketContext[] }>('/markets');
  }

  // --- Projects & Estimates ---
  async getProjects(token: string): Promise<{ data: Project[] }> {
    return this.client.get<{ data: Project[] }>('/projects', { token });
  }

  async createProject(project: Project, token: string): Promise<Project> {
    return this.client.post<Project>('/projects', project, { token });
  }

  async getEstimates(token: string): Promise<{ data: Estimate[] }> {
    return this.client.get<{ data: Estimate[] }>('/estimates', { token });
  }

  async createEstimate(estimate: Estimate, token: string): Promise<Estimate> {
    return this.client.post<Estimate>('/estimates', estimate, { token });
  }

  async getEstimate(estimateId: string, token: string): Promise<Estimate> {
    return this.client.get<Estimate>(`/estimates/${estimateId}`, { token });
  }

  // --- Calculations ---
  async calculate(request: CalculationRequest): Promise<CalculationResponse> {
    return this.client.post<CalculationResponse>('/calculations', request);
  }

  // --- Sync ---
  async syncDelta(request: SyncDeltaRequest, token: string): Promise<SyncDeltaResponse> {
    return this.client.post<SyncDeltaResponse>('/sync/delta', request, { token });
  }
}

export const api = new KonstrivoApi();
