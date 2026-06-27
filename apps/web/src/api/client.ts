export type DashboardSummary = {
  products: number;
  lowStockProducts: number;
  customers: number;
  employees: number;
  quotes: number;
  inventoryCostValue: number;
  salesToday: number;
  receivables: number;
};

export type EmployeeAccessModule =
  | 'POINT_OF_SALE'
  | 'SALES_HISTORY'
  | 'PRODUCTS'
  | 'REPLENISHMENT'
  | 'PURCHASES'
  | 'MOVEMENTS'
  | 'COUNTS'
  | 'ACCOUNTING'
  | 'CUSTOMERS'
  | 'SUPPLIERS'
  | 'QUOTES'
  | 'EMPLOYEES'
  | 'SETTINGS'
  | 'REPORTS';

export type EmployeeAccessLevel = 'NONE' | 'READ' | 'WRITE';

export type EmployeePermission = {
  module: EmployeeAccessModule;
  accessLevel: EmployeeAccessLevel;
};

export type Employee = {
  id: string;
  fullName: string;
  firstName: string;
  lastName: string;
  documentId?: string;
  position: string;
  department?: string;
  phone?: string;
  email?: string;
  hireDate?: string;
  salary: number;
  commissionRate: number;
  active: boolean;
  username?: string;
  userActive: boolean;
  allowWebAccess: boolean;
  permissions: EmployeePermission[];
};

export type EmployeeRequest = {
  firstName: string;
  lastName: string;
  documentId?: string;
  position: string;
  department?: string;
  phone?: string;
  email?: string;
  hireDate?: string;
  salary: number;
  commissionRate: number;
  active: boolean;
  username?: string;
  password?: string;
  userActive: boolean;
  allowWebAccess: boolean;
  permissions: EmployeePermission[];
};

export type AuthUser = {
  employeeId: string;
  fullName: string;
  username: string;
  allowWebAccess: boolean;
  mustChangePassword: boolean;
  permissions: EmployeePermission[];
};

export type AuthSession = {
  token: string;
  expiresAt: string;
  user: AuthUser;
};

export type Product = {
  id: string;
  sku: string;
  barcode?: string;
  name: string;
  description?: string;
  categoryName?: string;
  brandName?: string;
  unit: string;
  costPrice: number;
  salePrice: number;
  taxRate: number;
  currentStock: number;
  minimumStock: number;
  lowStock: boolean;
  active: boolean;
};

export type ProductRequest = {
  sku: string;
  barcode?: string;
  name: string;
  description?: string;
  categoryName?: string;
  brandName?: string;
  unit: string;
  costPrice: number;
  salePrice: number;
  taxRate: number;
  currentStock: number;
  minimumStock: number;
  active: boolean;
};

export type Supplier = {
  id: string;
  name: string;
  rnc?: string;
  phone?: string;
  email?: string;
  address?: string;
  active: boolean;
};

export type SupplierRequest = {
  name: string;
  rnc?: string;
  phone?: string;
  email?: string;
  address?: string;
  active: boolean;
};

export type InventoryMovementType = 'PURCHASE' | 'ADJUSTMENT_IN' | 'ADJUSTMENT_OUT' | 'SALE' | 'RETURN';

export type InventoryMovement = {
  id: string;
  productId: string;
  productSku: string;
  productName: string;
  movementType: InventoryMovementType;
  quantity: number;
  unitCost?: number;
  reference?: string;
  notes?: string;
  createdAt: string;
};

export type InventoryMovementRequest = {
  productId: string;
  movementType: InventoryMovementType;
  quantity: number;
  unitCost?: number;
  reference?: string;
  notes?: string;
};

export type InventoryMovementFilters = {
  search?: string;
  movementType?: InventoryMovementType | '';
  dateFrom?: string;
  dateTo?: string;
};

export type InventoryCount = {
  id: string;
  countNumber: string;
  countedAt: string;
  status: 'POSTED';
  notes?: string;
  productsCounted: number;
  productsWithDifference: number;
  netDifference: number;
  items: Array<{
    id: string;
    productId: string;
    productSku: string;
    productName: string;
    expectedStock: number;
    countedStock: number;
    difference: number;
  }>;
};

export type InventoryCountRequest = {
  notes?: string;
  items: Array<{
    productId: string;
    countedStock: number;
  }>;
};

export type PurchasePaymentTerm = 'CASH' | 'CREDIT';

export type PurchaseInvoice = {
  id: string;
  purchaseOrderId?: string;
  purchaseOrderNumber?: string;
  supplierId: string;
  supplierName: string;
  supplierRnc?: string;
  documentNumber: string;
  invoiceDate: string;
  dueDate?: string;
  paymentTerm: PurchasePaymentTerm;
  status: 'POSTED' | 'CANCELLED';
  subtotal: number;
  taxTotal: number;
  total: number;
  notes?: string;
  createdAt: string;
  items: Array<{
    id: string;
    productId: string;
    productName: string;
    quantity: number;
    unitCost: number;
    taxRate: number;
    subtotal: number;
    taxAmount: number;
    lineTotal: number;
  }>;
};

export type PurchaseInvoiceRequest = {
  purchaseOrderId?: string;
  supplierId: string;
  documentNumber: string;
  invoiceDate: string;
  dueDate?: string;
  paymentTerm: PurchasePaymentTerm;
  notes?: string;
  items: Array<{
    productId: string;
    quantity: number;
    unitCost: number;
    taxRate: number;
  }>;
};

export type PurchaseOrderStatus = 'OPEN' | 'RECEIVED' | 'CANCELLED';

export type PurchaseOrder = {
  id: string;
  supplierId: string;
  supplierName: string;
  supplierRnc?: string;
  orderNumber: string;
  orderDate: string;
  expectedDate?: string;
  status: PurchaseOrderStatus;
  subtotal: number;
  taxTotal: number;
  total: number;
  notes?: string;
  createdAt: string;
  items: Array<{
    id: string;
    productId: string;
    productSku: string;
    productName: string;
    quantity: number;
    unitCost: number;
    taxRate: number;
    subtotal: number;
    taxAmount: number;
    lineTotal: number;
  }>;
};

export type PurchaseOrderRequest = {
  supplierId: string;
  expectedDate?: string;
  notes?: string;
  items: Array<{
    productId: string;
    quantity: number;
    unitCost: number;
    taxRate: number;
  }>;
};

export type ReportFormat = 'pdf' | 'xlsx' | 'docx';
export type InventoryReportSection = 'products' | 'replenishment' | 'purchases' | 'movements' | 'counts' | 'suppliers';
export type QuoteStatus = 'DRAFT' | 'SENT' | 'APPROVED' | 'EXPIRED' | 'CONVERTED' | 'CANCELLED';

export type Quote = {
  id: string;
  quoteNumber: string;
  customerName: string;
  customerFiscalId?: string;
  issueDate: string;
  validUntil: string;
  status: QuoteStatus;
  subtotal: number;
  taxTotal: number;
  total: number;
  notes?: string;
  convertedSaleId?: string;
  convertedAt?: string;
  items: Array<{
    id: string;
    productId?: string;
    productName: string;
    quantity: number;
    unitPrice: number;
    taxRate: number;
    subtotal: number;
    taxAmount: number;
    lineTotal: number;
  }>;
};

export type QuoteRequest = {
  customerName: string;
  customerFiscalId?: string;
  issueDate: string;
  validUntil: string;
  status: QuoteStatus;
  notes?: string;
  items: Array<{
    productId?: string;
    productName: string;
    quantity: number;
    unitPrice: number;
    taxRate: number;
  }>;
};

export type CustomerType = 'FINAL' | 'EMPRESARIAL';

export type CustomerFiscalProfile = 'STANDARD' | 'TAX_CREDIT' | 'FREE_ZONE' | 'GOVERNMENT' | 'SPECIAL_REGIME';

export type Customer = {
  id: string;
  name: string;
  type: CustomerType;
  fiscalId?: string;
  fiscalProfile: CustomerFiscalProfile;
  phone?: string;
  email?: string;
  address?: string;
  creditLimit: number;
  active: boolean;
};

export type CustomerRequest = {
  name: string;
  type: CustomerType;
  fiscalId?: string;
  fiscalProfile: CustomerFiscalProfile;
  phone?: string;
  email?: string;
  address?: string;
  creditLimit: number;
  active: boolean;
};

export type PaymentMethod = 'CASH' | 'CARD' | 'TRANSFER' | 'CREDIT';

export type PaymentProcessor = 'AZUL' | 'CARDNET';

export type TaxpayerLookup = {
  rnc: string;
  name: string;
  razonSocial?: string;
  nombreComercial?: string;
  categoria?: string;
  regimenPago?: string;
  status?: string;
  estado?: string;
  actividadEconomica?: string;
  fechaConstitucion?: string;
  administracionLocal?: string;
  actualizadoEn?: string;
  fiscalProfile?: CustomerFiscalProfile;
  source: string;
  verified: boolean;
};

export type SaleRequest = {
  customerId?: string;
  customerName: string;
  customerFiscalId?: string;
  discountTotal: number;
  items: Array<{
    productId: string;
    quantity: number;
  }>;
  payments: Array<{
    method: PaymentMethod;
    amount: number;
    processor?: PaymentProcessor;
    reference?: string;
  }>;
};

export type SaleResponse = {
  id: string;
  invoiceNumber: string;
  ncf?: string;
  fiscalDocumentType?: FiscalDocumentType;
  sourceQuoteId?: string;
  ecfStatus?: EcfStatus;
  ecfTrackId?: string;
  ecfSecurityCode?: string;
  customerName: string;
  customerFiscalId?: string;
  subtotal: number;
  taxTotal: number;
  discountTotal: number;
  total: number;
  issuedAt: string;
  items: Array<{
    id: string;
    productName: string;
    quantity: number;
    unitPrice: number;
    taxRate: number;
    subtotal: number;
    taxAmount: number;
    lineTotal: number;
  }>;
  payments: Array<{
    id: string;
    method: PaymentMethod;
    amount: number;
    processor?: PaymentProcessor;
    reference?: string;
  }>;
};

export type FiscalDocumentType =
  | 'CONSUMO'
  | 'CREDITO_FISCAL'
  | 'NOTA_CREDITO'
  | 'NOTA_DEBITO'
  | 'GUBERNAMENTAL'
  | 'REGIMEN_ESPECIAL';

export type EcfStatus = 'NOT_SUBMITTED' | 'READY_TO_SIGN' | 'SIGNED' | 'SUBMITTED' | 'ACCEPTED' | 'REJECTED';

export type CompanyProfile = {
  id: string;
  name: string;
  commercialName?: string;
  rnc?: string;
  phone?: string;
  email?: string;
  address?: string;
  logoUrl?: string;
  currencyCode: string;
  taxRate: number;
};

export type CompanyRequest = Omit<CompanyProfile, 'id'>;

export type NcfSequence = {
  id: string;
  documentType: FiscalDocumentType;
  prefix: string;
  currentNumber: number;
  endNumber: number;
  validUntil: string;
  active: boolean;
};

export type EcfDocument = {
  id: string;
  saleId: string;
  invoiceNumber: string;
  ncf?: string;
  fiscalDocumentType: FiscalDocumentType;
  status: EcfStatus;
  unsignedXml: string;
  signedXml?: string;
  xmlHash?: string;
  signatureValue?: string;
  trackId?: string;
  securityCode?: string;
  acknowledgementXml?: string;
  generatedAt: string;
  submittedAt?: string;
  acceptedAt?: string;
};

export type AccountingDocument = {
  id: string;
  partyName: string;
  documentNumber?: string;
  amount: number;
  balance: number;
  dueDate?: string;
  status: string;
};

export type AccountingPaymentMethod = 'CASH' | 'BANK_TRANSFER' | 'CARD' | 'CHECK' | 'OTHER';

export type AccountingPaymentRequest = {
  amount: number;
  paymentDate: string;
  method: AccountingPaymentMethod;
  journalCode?: string;
  reference?: string;
  notes?: string;
};

export type AccountingPayment = {
  id: string;
  direction: 'CUSTOMER' | 'VENDOR';
  receivableId?: string;
  payableId?: string;
  journalCode: string;
  journalName: string;
  paymentDate: string;
  method: AccountingPaymentMethod;
  partyName: string;
  amount: number;
  reference?: string;
  notes?: string;
  reconciled: boolean;
};

export type AccountingSummary = {
  receivablesBalance: number;
  payablesBalance: number;
  netPosition: number;
  receivables: AccountingDocument[];
  payables: AccountingDocument[];
};

export type AccountingAccount = {
  id: string;
  code: string;
  name: string;
  accountType: string;
  normalBalance: 'DEBIT' | 'CREDIT';
  allowReconciliation: boolean;
  active: boolean;
};

export type AccountingAccountRequest = Omit<AccountingAccount, 'id'>;

export type AccountingJournal = {
  id: string;
  code: string;
  name: string;
  journalType: string;
  active: boolean;
};

export type AccountingJournalRequest = Omit<AccountingJournal, 'id'>;

export type JournalEntryLine = {
  id: string;
  accountCode: string;
  accountName: string;
  label: string;
  partnerName?: string;
  debit: number;
  credit: number;
};

export type JournalEntry = {
  id: string;
  journalCode: string;
  journalName: string;
  entryNumber: string;
  entryDate: string;
  reference?: string;
  sourceType?: string;
  sourceId?: string;
  status: string;
  totalDebit: number;
  totalCredit: number;
  notes?: string;
  lines: JournalEntryLine[];
};

export type TrialBalanceLine = {
  accountCode: string;
  accountName: string;
  accountType: string;
  normalBalance: 'DEBIT' | 'CREDIT';
  debit: number;
  credit: number;
  balance: number;
};

export type ManualJournalEntryRequest = {
  journalCode: string;
  entryDate: string;
  reference?: string;
  notes?: string;
  lines: Array<{
    accountCode: string;
    label: string;
    partnerName?: string;
    debit: number;
    credit: number;
  }>;
};

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
};

const AUTH_TOKEN_KEY = 'peralta_pos_auth_token';
const isLocalBrowser = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL
  ?? (isLocalBrowser || (window.location.protocol !== 'http:' && window.location.protocol !== 'https:')
    ? 'http://localhost:8080/api'
    : '/api');
let authToken = window.localStorage.getItem(AUTH_TOKEN_KEY) ?? '';

export function getAuthToken() {
  return authToken;
}

export function setAuthToken(token: string) {
  authToken = token;
  window.localStorage.setItem(AUTH_TOKEN_KEY, token);
}

export function clearAuthToken() {
  authToken = '';
  window.localStorage.removeItem(AUTH_TOKEN_KEY);
}

function withAuthHeaders(init: RequestInit = {}) {
  const headers = new Headers(init.headers);
  if (authToken) {
    headers.set('Authorization', `Bearer ${authToken}`);
  }
  return {
    ...init,
    headers
  };
}

const fetch = async (input: RequestInfo | URL, init: RequestInit = {}) => {
  const response = await window.fetch(input, withAuthHeaders(init));
  if (response.status === 401) {
    clearAuthToken();
  }
  return response;
};

async function readApiResponse<T>(response: Response, fallbackMessage: string): Promise<ApiResponse<T>> {
  const text = await response.text();

  if (!text.trim()) {
    throw new Error(`${fallbackMessage}. El backend respondio vacio.`);
  }

  try {
    return JSON.parse(text) as ApiResponse<T>;
  } catch {
    throw new Error(`${fallbackMessage}. El backend no devolvio JSON valido.`);
  }
}

export async function login(username: string, password: string): Promise<AuthSession> {
  const response = await window.fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password, clientChannel: import.meta.env.VITE_CLIENT_CHANNEL ?? 'WEB' })
  });
  const payload = await readApiResponse<AuthSession>(response, 'No se pudo iniciar sesion');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'Usuario o contrasena incorrectos');
  }

  setAuthToken(payload.data.token);
  return payload.data;
}

export async function getCurrentUser(): Promise<AuthSession> {
  const response = await fetch(`${API_BASE_URL}/auth/me`);
  const payload = await readApiResponse<AuthSession>(response, 'No se pudo recuperar la sesion');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'Sesion vencida');
  }

  setAuthToken(payload.data.token);
  return payload.data;
}

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const response = await fetch(`${API_BASE_URL}/dashboard/summary`);

  if (!response.ok) {
    throw new Error('No se pudo cargar el dashboard');
  }

  const payload = await readApiResponse<DashboardSummary>(response, 'No se pudo cargar el dashboard');
  return payload.data;
}

export async function getCompanyProfile(): Promise<CompanyProfile> {
  const response = await fetch(`${API_BASE_URL}/company/profile`);
  const payload = await readApiResponse<CompanyProfile>(response, 'No se pudieron cargar los datos de la empresa');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudieron cargar los datos de la empresa');
  }

  return payload.data;
}

export async function updateCompanyProfile(request: CompanyRequest): Promise<CompanyProfile> {
  const response = await fetch(`${API_BASE_URL}/company/profile`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<CompanyProfile>(response, 'No se pudieron guardar los datos de la empresa');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudieron guardar los datos de la empresa');
  }

  return payload.data;
}

export async function getNcfSequences(): Promise<NcfSequence[]> {
  const response = await fetch(`${API_BASE_URL}/billing/ncf-sequences`);
  const payload = await readApiResponse<NcfSequence[]>(response, 'No se pudieron cargar las secuencias NCF');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudieron cargar las secuencias NCF');
  }

  return payload.data;
}

export async function generateSaleEcf(saleId: string): Promise<EcfDocument> {
  const response = await fetch(`${API_BASE_URL}/billing/ecf/sales/${saleId}/generate`, {
    method: 'POST'
  });
  const payload = await readApiResponse<EcfDocument>(response, 'No se pudo generar el XML e-CF simulado');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo generar el XML e-CF simulado');
  }

  return payload.data;
}

export async function submitSaleEcfSimulation(saleId: string): Promise<EcfDocument> {
  const response = await fetch(`${API_BASE_URL}/billing/ecf/sales/${saleId}/submit-simulated`, {
    method: 'POST'
  });
  const payload = await readApiResponse<EcfDocument>(response, 'No se pudo guardar el acuse simulado');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo guardar el acuse simulado');
  }

  return payload.data;
}

export async function getSaleEcf(saleId: string): Promise<EcfDocument> {
  const response = await fetch(`${API_BASE_URL}/billing/ecf/sales/${saleId}`);
  const payload = await readApiResponse<EcfDocument>(response, 'No se pudo cargar el e-CF simulado');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo cargar el e-CF simulado');
  }

  return payload.data;
}

export async function getAccountingSummary(): Promise<AccountingSummary> {
  const response = await fetch(`${API_BASE_URL}/accounting/summary`);
  const payload = await readApiResponse<AccountingSummary>(response, 'No se pudo cargar contabilidad');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo cargar contabilidad');
  }

  return payload.data;
}

export async function getAccountingAccounts(): Promise<AccountingAccount[]> {
  const response = await fetch(`${API_BASE_URL}/accounting/accounts`);
  const payload = await readApiResponse<AccountingAccount[]>(response, 'No se pudo cargar el catalogo de cuentas');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo cargar el catalogo de cuentas');
  }

  return payload.data;
}

export async function createAccountingAccount(request: AccountingAccountRequest): Promise<AccountingAccount> {
  const response = await fetch(`${API_BASE_URL}/accounting/accounts`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<AccountingAccount>(response, 'No se pudo crear la cuenta contable');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear la cuenta contable');
  }

  return payload.data;
}

export async function updateAccountingAccount(id: string, request: AccountingAccountRequest): Promise<AccountingAccount> {
  const response = await fetch(`${API_BASE_URL}/accounting/accounts/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<AccountingAccount>(response, 'No se pudo actualizar la cuenta contable');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar la cuenta contable');
  }

  return payload.data;
}

export async function getAccountingJournals(): Promise<AccountingJournal[]> {
  const response = await fetch(`${API_BASE_URL}/accounting/journals`);
  const payload = await readApiResponse<AccountingJournal[]>(response, 'No se pudieron cargar los diarios contables');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudieron cargar los diarios contables');
  }

  return payload.data;
}

export async function createAccountingJournal(request: AccountingJournalRequest): Promise<AccountingJournal> {
  const response = await fetch(`${API_BASE_URL}/accounting/journals`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<AccountingJournal>(response, 'No se pudo crear el diario contable');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear el diario contable');
  }

  return payload.data;
}

export async function updateAccountingJournal(id: string, request: AccountingJournalRequest): Promise<AccountingJournal> {
  const response = await fetch(`${API_BASE_URL}/accounting/journals/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<AccountingJournal>(response, 'No se pudo actualizar el diario contable');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar el diario contable');
  }

  return payload.data;
}

export async function getJournalEntries(): Promise<JournalEntry[]> {
  const response = await fetch(`${API_BASE_URL}/accounting/entries`);
  const payload = await readApiResponse<JournalEntry[]>(response, 'No se pudieron cargar los asientos contables');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudieron cargar los asientos contables');
  }

  return payload.data;
}

export async function createManualJournalEntry(request: ManualJournalEntryRequest): Promise<JournalEntry> {
  const response = await fetch(`${API_BASE_URL}/accounting/entries/manual`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<JournalEntry>(response, 'No se pudo crear el asiento manual');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear el asiento manual');
  }

  return payload.data;
}

export async function getTrialBalance(): Promise<TrialBalanceLine[]> {
  const response = await fetch(`${API_BASE_URL}/accounting/trial-balance`);
  const payload = await readApiResponse<TrialBalanceLine[]>(response, 'No se pudo cargar el balance de comprobacion');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo cargar el balance de comprobacion');
  }

  return payload.data;
}

export async function getAccountingPayments(): Promise<AccountingPayment[]> {
  const response = await fetch(`${API_BASE_URL}/accounting/payments`);
  const payload = await readApiResponse<AccountingPayment[]>(response, 'No se pudieron cargar los pagos contables');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudieron cargar los pagos contables');
  }

  return payload.data;
}

export async function registerReceivablePayment(id: string, request: AccountingPaymentRequest): Promise<AccountingPayment> {
  const response = await fetch(`${API_BASE_URL}/accounting/receivables/${id}/payments`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<AccountingPayment>(response, 'No se pudo registrar el cobro');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo registrar el cobro');
  }

  return payload.data;
}

export async function registerPayablePayment(id: string, request: AccountingPaymentRequest): Promise<AccountingPayment> {
  const response = await fetch(`${API_BASE_URL}/accounting/payables/${id}/payments`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<AccountingPayment>(response, 'No se pudo registrar el pago');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo registrar el pago');
  }

  return payload.data;
}

export async function getEmployees(search = ''): Promise<Employee[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set('search', search.trim());
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/employees${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar los empleados');
    throw new Error(payload.message || 'No se pudieron cargar los empleados');
  }

  const payload = await readApiResponse<Employee[]>(response, 'No se pudieron cargar los empleados');
  return payload.data;
}

export async function createEmployee(request: EmployeeRequest): Promise<Employee> {
  const response = await fetch(`${API_BASE_URL}/employees`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Employee>(response, 'No se pudo crear el empleado');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear el empleado');
  }

  return payload.data;
}

export async function updateEmployee(id: string, request: EmployeeRequest): Promise<Employee> {
  const response = await fetch(`${API_BASE_URL}/employees/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Employee>(response, 'No se pudo actualizar el empleado');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar el empleado');
  }

  return payload.data;
}

export async function getProducts(): Promise<Product[]> {
  const response = await fetch(`${API_BASE_URL}/products`);

  if (!response.ok) {
    throw new Error('No se pudieron cargar los productos');
  }

  const payload = await readApiResponse<Product[]>(response, 'No se pudieron cargar los productos');
  return payload.data;
}

export async function createProduct(request: ProductRequest): Promise<Product> {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Product>(response, 'No se pudo crear el producto');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear el producto');
  }

  return payload.data;
}

export async function updateProduct(id: string, request: ProductRequest): Promise<Product> {
  const response = await fetch(`${API_BASE_URL}/products/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Product>(response, 'No se pudo actualizar el producto');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar el producto');
  }

  return payload.data;
}

export async function getSuppliers(search = ''): Promise<Supplier[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set('search', search.trim());
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/suppliers${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar los suplidores');
    throw new Error(payload.message || 'No se pudieron cargar los suplidores');
  }

  const payload = await readApiResponse<Supplier[]>(response, 'No se pudieron cargar los suplidores');
  return payload.data;
}

export async function createSupplier(request: SupplierRequest): Promise<Supplier> {
  const response = await fetch(`${API_BASE_URL}/suppliers`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Supplier>(response, 'No se pudo crear el suplidor');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear el suplidor');
  }

  return payload.data;
}

export async function updateSupplier(id: string, request: SupplierRequest): Promise<Supplier> {
  const response = await fetch(`${API_BASE_URL}/suppliers/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Supplier>(response, 'No se pudo actualizar el suplidor');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar el suplidor');
  }

  return payload.data;
}

export async function deactivateSupplier(id: string): Promise<Supplier> {
  const response = await fetch(`${API_BASE_URL}/suppliers/${id}`, {
    method: 'DELETE'
  });

  const payload = await readApiResponse<Supplier>(response, 'No se pudo desactivar el suplidor');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo desactivar el suplidor');
  }

  return payload.data;
}

export async function getInventoryMovements(filters: InventoryMovementFilters = {}): Promise<InventoryMovement[]> {
  const params = new URLSearchParams();

  if (filters.search?.trim()) {
    params.set('search', filters.search.trim());
  }
  if (filters.movementType) {
    params.set('movementType', filters.movementType);
  }
  if (filters.dateFrom) {
    params.set('dateFrom', filters.dateFrom);
  }
  if (filters.dateTo) {
    params.set('dateTo', filters.dateTo);
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/inventory/movements${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar los movimientos');
    throw new Error(payload.message || 'No se pudieron cargar los movimientos');
  }

  const payload = await readApiResponse<InventoryMovement[]>(response, 'No se pudieron cargar los movimientos');
  return payload.data;
}

export async function getInventoryCounts(): Promise<InventoryCount[]> {
  const response = await fetch(`${API_BASE_URL}/inventory/counts`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar los conteos');
    throw new Error(payload.message || 'No se pudieron cargar los conteos');
  }

  const payload = await readApiResponse<InventoryCount[]>(response, 'No se pudieron cargar los conteos');
  return payload.data;
}

export async function createInventoryCount(request: InventoryCountRequest): Promise<InventoryCount> {
  const response = await fetch(`${API_BASE_URL}/inventory/counts`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<InventoryCount>(response, 'No se pudo registrar el conteo');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo registrar el conteo');
  }

  return payload.data;
}

export async function createInventoryMovement(request: InventoryMovementRequest): Promise<InventoryMovement> {
  const response = await fetch(`${API_BASE_URL}/inventory/movements`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<InventoryMovement>(response, 'No se pudo registrar el movimiento');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo registrar el movimiento');
  }

  return payload.data;
}

export async function getPurchaseInvoices(search = ''): Promise<PurchaseInvoice[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set('search', search.trim());
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/purchases/invoices${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar las facturas de compra');
    throw new Error(payload.message || 'No se pudieron cargar las facturas de compra');
  }

  const payload = await readApiResponse<PurchaseInvoice[]>(response, 'No se pudieron cargar las facturas de compra');
  return payload.data;
}

export async function createPurchaseInvoice(request: PurchaseInvoiceRequest): Promise<PurchaseInvoice> {
  const response = await fetch(`${API_BASE_URL}/purchases/invoices`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<PurchaseInvoice>(response, 'No se pudo registrar la factura de compra');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo registrar la factura de compra');
  }

  return payload.data;
}

export async function getPurchaseOrders(search = ''): Promise<PurchaseOrder[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set('search', search.trim());
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/purchases/orders${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar las ordenes de compra');
    throw new Error(payload.message || 'No se pudieron cargar las ordenes de compra');
  }

  const payload = await readApiResponse<PurchaseOrder[]>(response, 'No se pudieron cargar las ordenes de compra');
  return payload.data;
}

export async function createPurchaseOrder(request: PurchaseOrderRequest): Promise<PurchaseOrder> {
  const response = await fetch(`${API_BASE_URL}/purchases/orders`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<PurchaseOrder>(response, 'No se pudo crear la orden de compra');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear la orden de compra');
  }

  return payload.data;
}

export async function cancelPurchaseOrder(id: string): Promise<PurchaseOrder> {
  const response = await fetch(`${API_BASE_URL}/purchases/orders/${id}/cancel`, {
    method: 'PUT'
  });

  const payload = await readApiResponse<PurchaseOrder>(response, 'No se pudo cancelar la orden de compra');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo cancelar la orden de compra');
  }

  return payload.data;
}

export async function downloadInventoryReport(
  section: InventoryReportSection,
  format: ReportFormat,
  includeCosts: boolean
): Promise<Blob> {
  const params = new URLSearchParams({
    includeCosts: String(includeCosts)
  });
  const response = await fetch(`${API_BASE_URL}/reports/inventory/${section}.${format}?${params}`);

  if (!response.ok) {
    throw new Error('No se pudo generar el reporte de esta seccion');
  }

  return response.blob();
}

export async function downloadPurchaseOrderReport(
  id: string,
  format: ReportFormat,
  inline = false
): Promise<Blob> {
  const params = new URLSearchParams();
  if (inline) {
    params.set('inline', 'true');
  }
  const query = params.toString();
  const response = await fetch(
    `${API_BASE_URL}/reports/purchase-orders/${id}.${format}${query ? `?${query}` : ''}`
  );

  if (!response.ok) {
    throw new Error('No se pudo generar el documento de la orden');
  }

  return response.blob();
}

export async function getQuotes(search = ''): Promise<Quote[]> {
  const params = new URLSearchParams();
  if (search.trim()) {
    params.set('search', search.trim());
  }
  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/quotes${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar las cotizaciones');
    throw new Error(payload.message || 'No se pudieron cargar las cotizaciones');
  }

  const payload = await readApiResponse<Quote[]>(response, 'No se pudieron cargar las cotizaciones');
  return payload.data;
}

export async function createQuote(request: QuoteRequest): Promise<Quote> {
  const response = await fetch(`${API_BASE_URL}/quotes`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<Quote>(response, 'No se pudo crear la cotizacion');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear la cotizacion');
  }

  return payload.data;
}

export async function updateQuote(id: string, request: QuoteRequest): Promise<Quote> {
  const response = await fetch(`${API_BASE_URL}/quotes/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });
  const payload = await readApiResponse<Quote>(response, 'No se pudo actualizar la cotizacion');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar la cotizacion');
  }

  return payload.data;
}

export async function invoiceQuote(id: string): Promise<SaleResponse> {
  const response = await fetch(`${API_BASE_URL}/quotes/${id}/invoice`, {
    method: 'POST'
  });
  const payload = await readApiResponse<SaleResponse>(response, 'No se pudo facturar la cotizacion');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo facturar la cotizacion');
  }

  return payload.data;
}

export async function downloadQuoteReport(
  id: string,
  format: ReportFormat,
  inline = false
): Promise<Blob> {
  const params = new URLSearchParams();
  if (inline) {
    params.set('inline', 'true');
  }
  const query = params.toString();
  const response = await fetch(
    `${API_BASE_URL}/reports/quotes/${id}.${format}${query ? `?${query}` : ''}`
  );

  if (!response.ok) {
    throw new Error('No se pudo generar el documento de la cotizacion');
  }

  return response.blob();
}

export async function getCustomers(search = ''): Promise<Customer[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set('search', search.trim());
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/customers${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar los clientes');
    throw new Error(payload.message || 'No se pudieron cargar los clientes');
  }

  const payload = await readApiResponse<Customer[]>(response, 'No se pudieron cargar los clientes');
  return payload.data;
}

export async function createCustomer(request: CustomerRequest): Promise<Customer> {
  const response = await fetch(`${API_BASE_URL}/customers`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Customer>(response, 'No se pudo crear el cliente');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo crear el cliente');
  }

  return payload.data;
}

export async function updateCustomer(id: string, request: CustomerRequest): Promise<Customer> {
  const response = await fetch(`${API_BASE_URL}/customers/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<Customer>(response, 'No se pudo actualizar el cliente');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo actualizar el cliente');
  }

  return payload.data;
}

export async function deactivateCustomer(id: string): Promise<Customer> {
  const response = await fetch(`${API_BASE_URL}/customers/${id}`, {
    method: 'DELETE'
  });

  const payload = await readApiResponse<Customer>(response, 'No se pudo desactivar el cliente');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo desactivar el cliente');
  }

  return payload.data;
}

export async function lookupTaxpayerByRnc(rnc: string): Promise<TaxpayerLookup> {
  const response = await fetch(`${API_BASE_URL}/dgii/rnc/${encodeURIComponent(rnc)}`);
  const payload = await readApiResponse<TaxpayerLookup>(response, 'No se pudo consultar el RNC');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo consultar el RNC');
  }

  return payload.data;
}

export async function createSale(request: SaleRequest): Promise<SaleResponse> {
  const response = await fetch(`${API_BASE_URL}/sales`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(request)
  });

  const payload = await readApiResponse<SaleResponse>(response, 'No se pudo registrar la venta');

  if (!response.ok || !payload.success) {
    throw new Error(payload.message || 'No se pudo registrar la venta');
  }

  return payload.data;
}

export async function getSales(search = ''): Promise<SaleResponse[]> {
  const params = new URLSearchParams();

  if (search.trim()) {
    params.set('search', search.trim());
  }

  const query = params.toString();
  const response = await fetch(`${API_BASE_URL}/sales${query ? `?${query}` : ''}`);

  if (!response.ok) {
    const payload = await readApiResponse<unknown>(response, 'No se pudieron cargar las ventas');
    throw new Error(payload.message || 'No se pudieron cargar las ventas');
  }

  const payload = await readApiResponse<SaleResponse[]>(response, 'No se pudieron cargar las ventas');
  return payload.data;
}
