import {
  Ban,
  Building2,
  Calculator,
  CheckCircle2,
  ClipboardCheck,
  CreditCard,
  Download,
  Edit3,
  Eye,
  FileSpreadsheet,
  FileText,
  Globe2,
  LayoutDashboard,
  LockKeyhole,
  LogOut,
  Minus,
  MonitorDot,
  Package,
  Plus,
  Printer,
  ReceiptText,
  RefreshCw,
  Save,
  Search,
  Settings,
  ShieldCheck,
  ShoppingCart,
  Trash2,
  UserRoundCheck,
  UserPlus,
  Users,
  Warehouse,
  X
} from 'lucide-react';
import { FormEvent, useEffect, useMemo, useState } from 'react';
import {
  createInventoryMovement,
  createInventoryCount,
  createCustomer,
  createEmployee,
  createProduct,
  createPurchaseInvoice,
  createPurchaseOrder,
  createQuote,
  createSale,
  createSupplier,
  AccountingAccount,
  AccountingAccountRequest,
  AccountingDocument,
  AccountingJournal,
  AccountingJournalRequest,
  AccountingPayment,
  AccountingPaymentMethod,
  AccountingPaymentRequest,
  AccountingSummary,
  AuthUser,
  clearAuthToken,
  CompanyProfile,
  CompanyRequest,
  Customer,
  CustomerFiscalProfile,
  CustomerRequest,
  CustomerType,
  DashboardSummary,
  deactivateCustomer,
  deactivateSupplier,
  cancelPurchaseOrder,
  downloadInventoryReport,
  downloadPurchaseOrderReport,
  downloadQuoteReport,
  Employee,
  EmployeeAccessLevel,
  EmployeeAccessModule,
  EmployeePermission,
  EmployeeRequest,
  getDashboardSummary,
  generateSaleEcf,
  getAccountingAccounts,
  getAccountingJournals,
  getAccountingPayments,
  getAccountingSummary,
  getAuthToken,
  getCompanyProfile,
  getCurrentUser,
  getCustomers,
  getEmployees,
  getInventoryMovements,
  getInventoryCounts,
  getProducts,
  getPurchaseInvoices,
  getPurchaseOrders,
  getQuotes,
  getSales,
  getJournalEntries,
  getSuppliers,
  getNcfSequences,
  getTrialBalance,
  JournalEntry,
  createAccountingAccount,
  createAccountingJournal,
  createManualJournalEntry,
  NcfSequence,
  TrialBalanceLine,
  InventoryMovement,
  InventoryCount,
  InventoryCountRequest,
  InventoryMovementRequest,
  InventoryMovementType,
  invoiceQuote,
  login,
  lookupTaxpayerByRnc,
  PaymentMethod,
  PaymentProcessor,
  Product,
  ProductRequest,
  PurchaseInvoice,
  PurchaseInvoiceRequest,
  PurchaseOrder,
  PurchaseOrderRequest,
  Quote,
  QuoteRequest,
  QuoteStatus,
  ReportFormat,
  PurchasePaymentTerm,
  registerPayablePayment,
  registerReceivablePayment,
  SaleResponse,
  Supplier,
  SupplierRequest,
  submitSaleEcfSimulation,
  updateCompanyProfile,
  updateAccountingAccount,
  updateAccountingJournal,
  updateCustomer,
  updateEmployee,
  updateProduct,
  updateQuote,
  updateSupplier
} from './api/client';

type ModuleKey =
  | 'dashboard'
  | 'pos'
  | 'sales'
  | 'inventory'
  | 'customers'
  | 'employees'
  | 'quotes'
  | 'billing'
  | 'accounting'
  | 'settings';

type CartItem = {
  product: Product;
  quantity: number;
};

type CustomerFormState = {
  id?: string;
  name: string;
  type: CustomerType;
  fiscalProfile: CustomerFiscalProfile;
  fiscalId: string;
  phone: string;
  email: string;
  address: string;
  creditLimit: number;
  active: boolean;
};

type ProductFormState = {
  id?: string;
  sku: string;
  barcode: string;
  name: string;
  description: string;
  categoryName: string;
  brandName: string;
  unit: string;
  costPrice: number;
  salePrice: number;
  taxRate: number;
  currentStock: number;
  minimumStock: number;
  active: boolean;
};

type SupplierFormState = {
  id?: string;
  name: string;
  rnc: string;
  phone: string;
  email: string;
  address: string;
  active: boolean;
};

type InventoryMovementFormState = {
  productId: string;
  movementType: InventoryMovementType;
  quantity: number;
  unitCost: number;
  reference: string;
  notes: string;
};

type PurchaseLineFormState = {
  key: string;
  productId: string;
  quantity: number;
  unitCost: number;
  taxRate: number;
};

type PurchaseInvoiceFormState = {
  purchaseOrderId?: string;
  supplierId: string;
  documentNumber: string;
  invoiceDate: string;
  dueDate: string;
  paymentTerm: PurchasePaymentTerm;
  notes: string;
  items: PurchaseLineFormState[];
};

type PurchaseOrderFormState = {
  supplierId: string;
  expectedDate: string;
  notes: string;
  items: PurchaseLineFormState[];
};

type InventoryCountLineFormState = {
  key: string;
  productId: string;
  expectedStock: number;
  countedStock: number;
};

type InventoryCountFormState = {
  notes: string;
  items: InventoryCountLineFormState[];
};

type QuoteLineFormState = {
  key: string;
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  taxRate: number;
};

type QuoteFormState = {
  id?: string;
  customerId: string;
  customerName: string;
  customerFiscalId: string;
  issueDate: string;
  validUntil: string;
  status: QuoteStatus;
  notes: string;
  items: QuoteLineFormState[];
};

type EmployeeFormState = {
  id?: string;
  firstName: string;
  lastName: string;
  documentId: string;
  position: string;
  department: string;
  phone: string;
  email: string;
  hireDate: string;
  salary: number;
  commissionRate: number;
  active: boolean;
  username: string;
  password: string;
  userActive: boolean;
  allowWebAccess: boolean;
  permissions: Record<EmployeeAccessModule, EmployeeAccessLevel>;
};

type InventoryTab = 'products' | 'replenishment' | 'purchases' | 'movements' | 'counts' | 'suppliers';

const fallbackSummary: DashboardSummary = {
  products: 128,
  lowStockProducts: 9,
  customers: 42,
  employees: 8,
  quotes: 17,
  inventoryCostValue: 1842500,
  salesToday: 76450,
  receivables: 132800
};

const fallbackCompany: CompanyProfile = {
  id: 'demo-company',
  name: 'Peralta S.A.',
  commercialName: 'Ferreteria Peralta',
  rnc: '131000001',
  phone: '809-555-0101',
  email: 'contacto@peraltapos.local',
  address: 'Av. Principal #25, Santo Domingo',
  logoUrl: '',
  currencyCode: 'DOP',
  taxRate: 18
};

const fallbackProducts: Product[] = [
  {
    id: 'demo-ft-001',
    sku: 'FT-001',
    name: 'Cemento gris 42.5kg',
    unit: 'saco',
    costPrice: 385,
    salePrice: 465,
    taxRate: 18,
    currentStock: 84,
    minimumStock: 25,
    lowStock: false,
    active: true
  },
  {
    id: 'demo-ft-014',
    sku: 'FT-014',
    name: 'Varilla 3/8 x 20 pies',
    unit: 'unidad',
    costPrice: 250,
    salePrice: 315,
    taxRate: 18,
    currentStock: 18,
    minimumStock: 30,
    lowStock: true,
    active: true
  },
  {
    id: 'demo-el-204',
    sku: 'EL-204',
    name: 'Cable electrico THHN #12',
    unit: 'pie',
    costPrice: 17,
    salePrice: 28,
    taxRate: 18,
    currentStock: 320,
    minimumStock: 120,
    lowStock: false,
    active: true
  },
  {
    id: 'demo-pl-077',
    sku: 'PL-077',
    name: 'Tubo PVC 1/2 pulgada',
    unit: 'unidad',
    costPrice: 62,
    salePrice: 95,
    taxRate: 18,
    currentStock: 11,
    minimumStock: 40,
    lowStock: true,
    active: true
  }
];

const dashboardQuotes = [
  { number: 'COT-20260616-00017', customer: 'Constructora Duarte SRL', validUntil: '2026-06-24', total: 84520, status: 'Enviada' },
  { number: 'COT-20260616-00016', customer: 'Maria Rodriguez', validUntil: '2026-06-20', total: 12340, status: 'Borrador' },
  { number: 'COT-20260615-00015', customer: 'Ferreteria Los Pinos', validUntil: '2026-06-22', total: 231980, status: 'Aprobada' }
];

const dashboardEmployees = [
  { name: 'Ana Peralta', role: 'Administradora', status: 'Activa' },
  { name: 'Luis Mateo', role: 'Cajero', status: 'En turno' },
  { name: 'Rosa Jimenez', role: 'Almacen', status: 'Activa' }
];

type ModuleDefinition = { key: ModuleKey; label: string; icon: typeof LayoutDashboard };

const modules: ModuleDefinition[] = [
  { key: 'dashboard', label: 'Panel', icon: LayoutDashboard },
  { key: 'pos', label: 'Punto de venta', icon: CreditCard },
  { key: 'sales', label: 'Historial ventas', icon: ReceiptText },
  { key: 'inventory', label: 'Inventario', icon: Warehouse },
  { key: 'customers', label: 'Clientes', icon: Users },
  { key: 'employees', label: 'Empleados', icon: UserRoundCheck },
  { key: 'quotes', label: 'Cotizaciones', icon: FileText },
  { key: 'billing', label: 'Facturacion RD', icon: Globe2 },
  { key: 'accounting', label: 'Contabilidad', icon: Calculator },
  { key: 'settings', label: 'Configuracion', icon: Settings }
];

const modulePermissionRequirements: Partial<Record<ModuleKey, EmployeeAccessModule[]>> = {
  pos: ['POINT_OF_SALE'],
  sales: ['SALES_HISTORY'],
  inventory: ['PRODUCTS', 'REPLENISHMENT', 'PURCHASES', 'MOVEMENTS', 'COUNTS', 'SUPPLIERS'],
  customers: ['CUSTOMERS'],
  employees: ['EMPLOYEES'],
  quotes: ['QUOTES'],
  billing: ['ACCOUNTING'],
  accounting: ['ACCOUNTING'],
  settings: ['SETTINGS']
};

const inventoryTabPermissions: Record<InventoryTab, EmployeeAccessModule> = {
  products: 'PRODUCTS',
  replenishment: 'REPLENISHMENT',
  purchases: 'PURCHASES',
  movements: 'MOVEMENTS',
  counts: 'COUNTS',
  suppliers: 'SUPPLIERS'
};

const inventoryTabDetails: Array<{ key: InventoryTab; label: string; icon: typeof Package }> = [
  { key: 'products', label: 'Productos', icon: Package },
  { key: 'replenishment', label: 'Reposicion', icon: ShoppingCart },
  { key: 'purchases', label: 'Facturas de compra', icon: ReceiptText },
  { key: 'movements', label: 'Movimientos', icon: RefreshCw },
  { key: 'counts', label: 'Conteo fisico', icon: ClipboardCheck },
  { key: 'suppliers', label: 'Suplidores', icon: Building2 }
];

const inventoryPermissionModules: EmployeeAccessModule[] = [
  'PRODUCTS',
  'REPLENISHMENT',
  'PURCHASES',
  'MOVEMENTS',
  'COUNTS',
  'SUPPLIERS'
];

function permissionLevel(user: AuthUser | null, module: EmployeeAccessModule): EmployeeAccessLevel {
  return user?.permissions.find((permission) => permission.module === module)?.accessLevel ?? 'NONE';
}

function canReadPermission(user: AuthUser | null, module: EmployeeAccessModule) {
  const level = permissionLevel(user, module);
  return level === 'READ' || level === 'WRITE';
}

function canWritePermission(user: AuthUser | null, module: EmployeeAccessModule) {
  return permissionLevel(user, module) === 'WRITE';
}

function canAccessAppModule(user: AuthUser | null, moduleKey: ModuleKey) {
  if (moduleKey === 'dashboard') {
    return true;
  }

  const requirements = modulePermissionRequirements[moduleKey];
  return requirements?.some((permission) => canReadPermission(user, permission)) ?? false;
}

const employeePermissionModules: Array<{ key: EmployeeAccessModule; label: string; description: string }> = [
  { key: 'POINT_OF_SALE', label: 'Punto de venta', description: 'Registrar ventas y cobrar.' },
  { key: 'SALES_HISTORY', label: 'Historial de ventas', description: 'Consultar ventas anteriores.' },
  { key: 'PRODUCTS', label: 'Productos e inventario', description: 'Ver o mantener productos y existencias.' },
  { key: 'REPLENISHMENT', label: 'Reposicion', description: 'Ordenes de compra y seguimiento.' },
  { key: 'PURCHASES', label: 'Facturas de compra', description: 'Entradas de facturas de suplidores.' },
  { key: 'MOVEMENTS', label: 'Movimientos', description: 'Entradas, salidas y ajustes de inventario.' },
  { key: 'COUNTS', label: 'Conteos fisicos', description: 'Conteos, diferencias y ajustes.' },
  { key: 'ACCOUNTING', label: 'Contabilidad', description: 'Cuentas, cuadre y ajustes contables.' },
  { key: 'CUSTOMERS', label: 'Clientes', description: 'Directorio y datos fiscales de clientes.' },
  { key: 'SUPPLIERS', label: 'Suplidores', description: 'Directorio de suplidores.' },
  { key: 'QUOTES', label: 'Cotizaciones', description: 'Crear y aprobar cotizaciones.' },
  { key: 'EMPLOYEES', label: 'Empleados', description: 'Gestionar empleados, usuarios y permisos.' },
  { key: 'SETTINGS', label: 'Configuracion', description: 'Preferencias generales del sistema.' },
  { key: 'REPORTS', label: 'Reportes', description: 'Exportar PDF, Excel y Word.' }
];

const employeeAccessLabels: Record<EmployeeAccessLevel, string> = {
  NONE: 'Sin acceso',
  READ: 'Solo ver',
  WRITE: 'Ver y editar'
};

const employeePermissionTemplates: Array<{
  label: string;
  description: string;
  permissions: Partial<Record<EmployeeAccessModule, EmployeeAccessLevel>>;
}> = [
  {
    label: 'Administrador',
    description: 'Acceso completo a todo el sistema.',
    permissions: employeePermissionModules.reduce((template, module) => {
      template[module.key] = 'WRITE';
      return template;
    }, {} as Partial<Record<EmployeeAccessModule, EmployeeAccessLevel>>)
  },
  {
    label: 'Caja / ventas',
    description: 'Venta, clientes, cotizaciones y consulta de inventario.',
    permissions: {
      POINT_OF_SALE: 'WRITE',
      SALES_HISTORY: 'READ',
      PRODUCTS: 'READ',
      CUSTOMERS: 'WRITE',
      QUOTES: 'WRITE',
      REPORTS: 'READ'
    }
  },
  {
    label: 'Almacen',
    description: 'Inventario, reposicion, movimientos y conteos.',
    permissions: {
      PRODUCTS: 'WRITE',
      REPLENISHMENT: 'WRITE',
      PURCHASES: 'READ',
      MOVEMENTS: 'WRITE',
      COUNTS: 'WRITE',
      SUPPLIERS: 'READ',
      REPORTS: 'READ'
    }
  },
  {
    label: 'Solo consulta',
    description: 'Puede ver reportes y consultar datos, sin editar.',
    permissions: employeePermissionModules.reduce((template, module) => {
      template[module.key] = 'READ';
      return template;
    }, {} as Partial<Record<EmployeeAccessModule, EmployeeAccessLevel>>)
  }
];

const money = new Intl.NumberFormat('es-DO', {
  style: 'currency',
  currency: 'DOP',
  maximumFractionDigits: 0
});

const paymentLabels: Record<PaymentMethod, string> = {
  CASH: 'Efectivo',
  CARD: 'Tarjeta',
  TRANSFER: 'Transferencia',
  CREDIT: 'Credito'
};

const paymentProcessorLabels: Record<PaymentProcessor, string> = {
  AZUL: 'Azul',
  CARDNET: 'CardNet'
};

const ecfStatusLabels: Record<string, string> = {
  NOT_SUBMITTED: 'Preparada para e-CF',
  READY_TO_SIGN: 'Lista para firmar',
  SIGNED: 'Firmada',
  SUBMITTED: 'Enviada a DGII',
  ACCEPTED: 'Aceptada',
  REJECTED: 'Rechazada'
};

const customerTypeLabels: Record<CustomerType, string> = {
  FINAL: 'Consumidor final',
  EMPRESARIAL: 'Empresa / credito fiscal'
};

const customerFiscalProfileLabels: Record<CustomerFiscalProfile, string> = {
  STANDARD: 'Normal',
  TAX_CREDIT: 'Credito fiscal',
  FREE_ZONE: 'Zona franca',
  GOVERNMENT: 'Gubernamental',
  SPECIAL_REGIME: 'Regimen especial'
};

const movementTypeLabels: Record<InventoryMovementType, string> = {
  PURCHASE: 'Compra / entrada',
  ADJUSTMENT_IN: 'Ajuste entrada',
  ADJUSTMENT_OUT: 'Ajuste salida',
  SALE: 'Venta',
  RETURN: 'Devolucion'
};

const purchaseOrderStatusLabels: Record<PurchaseOrder['status'], string> = {
  OPEN: 'Abierta',
  RECEIVED: 'Recibida',
  CANCELLED: 'Cancelada'
};

const quoteStatusLabels: Record<QuoteStatus, string> = {
  DRAFT: 'Borrador',
  SENT: 'Enviada',
  APPROVED: 'Aprobada',
  EXPIRED: 'Vencida',
  CONVERTED: 'Convertida',
  CANCELLED: 'Cancelada'
};

const inventoryReportDetails: Record<InventoryTab, {
  title: string;
  description: string;
  filename: string;
  supportsCosts: boolean;
}> = {
  products: {
    title: 'Reporte de productos',
    description: 'Catalogo completo, precios, impuestos y existencias.',
    filename: 'productos',
    supportsCosts: true
  },
  replenishment: {
    title: 'Reporte de ordenes de compra',
    description: 'Ordenes abiertas, recibidas y canceladas.',
    filename: 'ordenes-compra',
    supportsCosts: false
  },
  purchases: {
    title: 'Reporte de facturas de compra',
    description: 'Facturas recibidas, condiciones y totales por suplidor.',
    filename: 'facturas-compra',
    supportsCosts: false
  },
  movements: {
    title: 'Reporte de movimientos',
    description: 'Entradas, salidas, ventas, devoluciones y ajustes.',
    filename: 'movimientos-inventario',
    supportsCosts: true
  },
  counts: {
    title: 'Reporte de conteos fisicos',
    description: 'Conteos realizados, diferencias y ajustes aplicados.',
    filename: 'conteos-fisicos',
    supportsCosts: false
  },
  suppliers: {
    title: 'Reporte de suplidores',
    description: 'Directorio, contactos y estado de cada suplidor.',
    filename: 'suplidores',
    supportsCosts: false
  }
};

function createBlankCustomerForm(): CustomerFormState {
  return {
    name: '',
    type: 'FINAL',
    fiscalProfile: 'STANDARD',
    fiscalId: '',
    phone: '',
    email: '',
    address: '',
    creditLimit: 0,
    active: true
  };
}

function createBlankProductForm(): ProductFormState {
  return {
    sku: '',
    barcode: '',
    name: '',
    description: '',
    categoryName: '',
    brandName: '',
    unit: 'unidad',
    costPrice: 0,
    salePrice: 0,
    taxRate: 18,
    currentStock: 0,
    minimumStock: 0,
    active: true
  };
}

function createBlankSupplierForm(): SupplierFormState {
  return {
    name: '',
    rnc: '',
    phone: '',
    email: '',
    address: '',
    active: true
  };
}

function createBlankMovementForm(): InventoryMovementFormState {
  return {
    productId: '',
    movementType: 'PURCHASE',
    quantity: 1,
    unitCost: 0,
    reference: '',
    notes: ''
  };
}

function createBlankPurchaseLine(): PurchaseLineFormState {
  return {
    key: `${Date.now()}-${Math.random()}`,
    productId: '',
    quantity: 1,
    unitCost: 0,
    taxRate: 18
  };
}

function createBlankPurchaseForm(): PurchaseInvoiceFormState {
  return {
    supplierId: '',
    documentNumber: '',
    invoiceDate: new Date().toISOString().slice(0, 10),
    dueDate: '',
    paymentTerm: 'CASH',
    notes: '',
    items: [createBlankPurchaseLine()]
  };
}

function createBlankPurchaseOrderForm(): PurchaseOrderFormState {
  return {
    supplierId: '',
    expectedDate: '',
    notes: '',
    items: [createBlankPurchaseLine()]
  };
}

function createBlankCountLine(): InventoryCountLineFormState {
  return {
    key: `${Date.now()}-${Math.random()}`,
    productId: '',
    expectedStock: 0,
    countedStock: 0
  };
}

function createBlankCountForm(): InventoryCountFormState {
  return {
    notes: '',
    items: [createBlankCountLine()]
  };
}

function createBlankQuoteLine(): QuoteLineFormState {
  return {
    key: `${Date.now()}-${Math.random()}`,
    productId: '',
    productName: '',
    quantity: 1,
    unitPrice: 0,
    taxRate: 18
  };
}

function dateAfterDays(days: number) {
  const date = new Date();
  date.setDate(date.getDate() + days);
  return date.toISOString().slice(0, 10);
}

function createBlankQuoteForm(): QuoteFormState {
  return {
    customerId: '',
    customerName: '',
    customerFiscalId: '',
    issueDate: new Date().toISOString().slice(0, 10),
    validUntil: dateAfterDays(7),
    status: 'DRAFT',
    notes: 'Precios validos hasta la fecha indicada. Sujeto a disponibilidad.',
    items: [createBlankQuoteLine()]
  };
}

export default function App() {
  const [activeModule, setActiveModule] = useState<ModuleKey>('dashboard');
  const [authUser, setAuthUser] = useState<AuthUser | null>(null);
  const [authLoading, setAuthLoading] = useState(true);
  const [authError, setAuthError] = useState('');
  const [summary, setSummary] = useState<DashboardSummary>(fallbackSummary);
  const [companyProfile, setCompanyProfile] = useState<CompanyProfile>(fallbackCompany);
  const [products, setProducts] = useState<Product[]>(fallbackProducts);
  const [apiOnline, setApiOnline] = useState(false);

  const availableModules = useMemo(() => {
    return modules.filter((module) => canAccessAppModule(authUser, module.key));
  }, [authUser]);

  const refreshBusinessData = async () => {
    const [summaryResult, productsResult, companyResult] = await Promise.allSettled([
      getDashboardSummary(),
      getProducts(),
      getCompanyProfile()
    ]);

    if (summaryResult.status === 'fulfilled') {
      setSummary(summaryResult.value);
    }

    if (productsResult.status === 'fulfilled') {
      setProducts(productsResult.value);
    } else {
      setProducts(fallbackProducts);
    }

    if (companyResult.status === 'fulfilled') {
      setCompanyProfile(companyResult.value);
    }

    setApiOnline(summaryResult.status === 'fulfilled' || productsResult.status === 'fulfilled');
  };

  useEffect(() => {
    let alive = true;

    const restoreSession = async () => {
      if (!getAuthToken()) {
        setAuthLoading(false);
        return;
      }

      try {
        const session = await getCurrentUser();
        if (alive) {
          setAuthUser(session.user);
          setAuthError('');
        }
      } catch (error) {
        clearAuthToken();
        if (alive) {
          setAuthError(error instanceof Error ? error.message : 'La sesion vencio. Inicia sesion de nuevo.');
        }
      } finally {
        if (alive) {
          setAuthLoading(false);
        }
      }
    };

    void restoreSession();

    return () => {
      alive = false;
    };
  }, []);

  useEffect(() => {
    if (authUser) {
      void refreshBusinessData();
    }
  }, [authUser]);

  useEffect(() => {
    if (!availableModules.some((module) => module.key === activeModule)) {
      setActiveModule('dashboard');
    }
  }, [activeModule, availableModules]);

  const activeTitle = useMemo(() => {
    return availableModules.find((module) => module.key === activeModule)?.label ?? 'Panel';
  }, [activeModule, availableModules]);

  const handleLogin = async (username: string, password: string) => {
    const session = await login(username, password);
    setAuthUser(session.user);
    setAuthError('');
    setActiveModule('dashboard');
  };

  const handleLogout = () => {
    clearAuthToken();
    setAuthUser(null);
    setAuthError('');
    setApiOnline(false);
    setSummary(fallbackSummary);
    setCompanyProfile(fallbackCompany);
    setProducts(fallbackProducts);
    setActiveModule('dashboard');
  };

  if (authLoading) {
    return <SessionSplash />;
  }

  if (!authUser) {
    return <LoginView initialError={authError} onLogin={handleLogin} />;
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-block">
          <div className="brand-mark">P</div>
          <div>
            <strong>Peralta POS</strong>
            <span>Ferreteria Peralta</span>
          </div>
        </div>

        <nav className="module-nav" aria-label="Modulos principales">
          {availableModules.map((module) => {
            const Icon = module.icon;
            return (
              <button
                key={module.key}
                className={module.key === activeModule ? 'nav-item active' : 'nav-item'}
                onClick={() => setActiveModule(module.key)}
                title={module.label}
              >
                <Icon size={18} />
                <span>{module.label}</span>
              </button>
            );
          })}
        </nav>
      </aside>

      <main className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">Sucursal principal - Caja 01</p>
            <h1>{activeTitle}</h1>
          </div>

          <div className="topbar-actions">
            <div className="user-pill">
              <ShieldCheck size={16} />
              <span>{authUser.fullName}</span>
            </div>
            <div className={apiOnline ? 'status-pill online' : 'status-pill'}>
              <MonitorDot size={16} />
              <span>{apiOnline ? 'API conectada' : 'Datos demo'}</span>
            </div>
            {canWritePermission(authUser, 'POINT_OF_SALE') && (
              <button className="primary-button" onClick={() => setActiveModule('pos')}>
                <Plus size={18} />
                <span>Nueva venta</span>
              </button>
            )}
            <button className="secondary-button" onClick={handleLogout}>
              <LogOut size={17} />
              <span>Salir</span>
            </button>
          </div>
        </header>

        {activeModule === 'dashboard' && <DashboardView summary={summary} products={products} />}
        {activeModule === 'pos' && (
          <PosView
            apiOnline={apiOnline}
            company={companyProfile}
            products={products.filter((product) => product.active)}
            onSaleCreated={refreshBusinessData}
          />
        )}
        {activeModule === 'sales' && <SalesHistoryView apiOnline={apiOnline} company={companyProfile} />}
        {activeModule === 'inventory' && (
          <InventoryView
            apiOnline={apiOnline}
            authUser={authUser}
            products={products}
            onInventoryChanged={refreshBusinessData}
          />
        )}
        {activeModule === 'customers' && <CustomersView apiOnline={apiOnline} onCustomersChanged={refreshBusinessData} />}
        {activeModule === 'employees' && <EmployeesView apiOnline={apiOnline} onEmployeesChanged={refreshBusinessData} />}
        {activeModule === 'quotes' && (
          <QuotesView
            apiOnline={apiOnline}
            company={companyProfile}
            products={products.filter((product) => product.active)}
            onQuoteInvoiced={refreshBusinessData}
          />
        )}
        {activeModule === 'billing' && <BillingView apiOnline={apiOnline} />}
        {activeModule === 'accounting' && <AccountingView apiOnline={apiOnline} />}
        {activeModule === 'settings' && (
          <SettingsView
            apiOnline={apiOnline}
            company={companyProfile}
            onCompanySaved={setCompanyProfile}
          />
        )}
        {activeModule !== 'dashboard'
          && activeModule !== 'pos'
          && activeModule !== 'sales'
          && activeModule !== 'inventory'
          && activeModule !== 'customers'
          && activeModule !== 'employees'
          && activeModule !== 'quotes'
          && activeModule !== 'billing'
          && activeModule !== 'accounting'
          && activeModule !== 'settings'
          && <ModulePlaceholder title={activeTitle} />}
      </main>
    </div>
  );
}

function SessionSplash() {
  return (
    <main className="login-shell">
      <section className="login-card">
        <div className="login-mark">
          <ShieldCheck size={30} />
        </div>
        <p className="eyebrow">Peralta POS</p>
        <h1>Preparando tu sesion...</h1>
        <p className="login-muted">Estamos verificando si ya tienes una sesion activa.</p>
      </section>
    </main>
  );
}

function LoginView({
  initialError,
  onLogin
}: {
  initialError: string;
  onLogin: (username: string, password: string) => Promise<void>;
}) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState(initialError);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    setMessage(initialError);
  }, [initialError]);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setBusy(true);
    setMessage('');

    try {
      await onLogin(username.trim(), password);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo iniciar sesion.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="login-shell">
      <section className="login-card">
        <div className="login-mark">
          <LockKeyhole size={30} />
        </div>
        <p className="eyebrow">Ferreteria Peralta</p>
        <h1>Entrar al sistema</h1>
        <p className="login-muted">
          Usa un usuario de empleado para que el menu y las acciones salgan segun sus permisos.
        </p>

        <form className="login-form" onSubmit={submit}>
          <label>
            Usuario
            <input
              autoComplete="username"
              autoFocus
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="usuario"
            />
          </label>
          <label>
            Contrasena
            <input
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Contrasena"
              type="password"
            />
          </label>
          {message && <div className="form-message">{message}</div>}
          <button className="primary-button login-submit" disabled={busy} type="submit">
            <ShieldCheck size={18} />
            <span>{busy ? 'Entrando...' : 'Iniciar sesion'}</span>
          </button>
        </form>
      </section>
    </main>
  );
}

function DashboardView({ summary, products }: { summary: DashboardSummary; products: Product[] }) {
  const criticalProducts = products.slice(0, 4);

  return (
    <>
      <section className="stats-grid" aria-label="Resumen ejecutivo">
        <MetricCard label="Ventas de hoy" value={money.format(summary.salesToday)} tone="green" />
        <MetricCard label="Valor inventario" value={money.format(summary.inventoryCostValue)} tone="blue" />
        <MetricCard label="Clientes" value={summary.customers.toString()} tone="sand" />
        <MetricCard label="Stock bajo" value={summary.lowStockProducts.toString()} tone="red" />
      </section>

      <section className="content-grid">
        <div className="panel wide">
          <div className="panel-heading">
            <div>
              <h2>Inventario critico</h2>
              <p>Productos que necesitan reposicion o revision de almacen.</p>
            </div>
            <button className="secondary-button">
              <Package size={17} />
              <span>Producto</span>
            </button>
          </div>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Codigo</th>
                  <th>Producto</th>
                  <th>Stock</th>
                  <th>Minimo</th>
                  <th>Precio</th>
                </tr>
              </thead>
              <tbody>
                {criticalProducts.map((product) => (
                  <tr key={product.sku}>
                    <td>{product.sku}</td>
                    <td>{product.name}</td>
                    <td>
                      <span className={product.lowStock ? 'stock low' : 'stock'}>{product.currentStock}</span>
                    </td>
                    <td>{product.minimumStock}</td>
                    <td>{money.format(product.salePrice)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="panel">
          <div className="panel-heading compact">
            <h2>Caja</h2>
            <ShieldCheck size={20} />
          </div>
          <div className="cash-summary">
            <span>Ventas de hoy</span>
            <strong>{money.format(summary.salesToday)}</strong>
            <small>Cajero: Luis Mateo</small>
          </div>
          <div className="action-row">
            <button className="secondary-button">
              <Printer size={17} />
              <span>Cierre</span>
            </button>
            <button className="secondary-button">
              <Download size={17} />
              <span>PDF</span>
            </button>
          </div>
        </div>

        <div className="panel wide">
          <div className="panel-heading">
            <div>
              <h2>Cotizaciones recientes</h2>
              <p>Documentos listos para enviar, imprimir o convertir a venta.</p>
            </div>
            <div className="segmented-actions" aria-label="Exportaciones">
              <button title="Exportar PDF">
                <FileText size={17} />
              </button>
              <button title="Exportar Excel">
                <FileSpreadsheet size={17} />
              </button>
              <button title="Imprimir">
                <Printer size={17} />
              </button>
            </div>
          </div>

          <div className="quote-list">
            {dashboardQuotes.map((quote) => (
              <article className="quote-row" key={quote.number}>
                <div>
                  <strong>{quote.number}</strong>
                  <span>{quote.customer}</span>
                </div>
                <div>
                  <small>Valida hasta</small>
                  <span>{quote.validUntil}</span>
                </div>
                <div>
                  <small>Total</small>
                  <span>{money.format(quote.total)}</span>
                </div>
                <span className="badge">{quote.status}</span>
              </article>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panel-heading compact">
            <h2>Equipo</h2>
            <Building2 size={20} />
          </div>
          <div className="employee-list">
            {dashboardEmployees.map((employee) => (
              <div className="employee-row" key={employee.name}>
                <div>
                  <strong>{employee.name}</strong>
                  <span>{employee.role}</span>
                </div>
                <small>{employee.status}</small>
              </div>
            ))}
          </div>
        </div>
      </section>
    </>
  );
}

function CustomersView({
  apiOnline,
  onCustomersChanged
}: {
  apiOnline: boolean;
  onCustomersChanged: () => void;
}) {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [form, setForm] = useState<CustomerFormState>(createBlankCustomerForm());
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [lookingUpTaxpayer, setLookingUpTaxpayer] = useState(false);
  const [message, setMessage] = useState('');

  const activeCustomers = customers.filter((customer) => customer.active).length;

  const loadCustomers = async (term = search) => {
    if (!apiOnline) {
      setCustomers([]);
      setMessage('El backend debe estar conectado para gestionar clientes.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getCustomers(term);
      setCustomers(data);
      if (data.length === 0) {
        setMessage('No hay clientes para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar los clientes.');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setForm(createBlankCustomerForm());
    setMessage('');
  };

  const editCustomer = (customer: Customer) => {
    setForm(customerToForm(customer));
    setMessage('');
  };

  const submitCustomer = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar clientes.');
      return;
    }

    if (!form.name.trim()) {
      setMessage('El nombre del cliente es obligatorio.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const request = customerFormToRequest(form);
      const saved = form.id
        ? await updateCustomer(form.id, request)
        : await createCustomer(request);

      setForm(customerToForm(saved));
      setMessage(`Cliente guardado: ${saved.name}.`);
      await loadCustomers(search);
      onCustomersChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar el cliente.');
    } finally {
      setSaving(false);
    }
  };

  const fillCustomerFromTaxpayer = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para consultar RNC.');
      return;
    }

    if (!form.fiscalId.trim()) {
      setMessage('Escribe el RNC o cedula antes de consultar.');
      return;
    }

    setLookingUpTaxpayer(true);
    setMessage('');

    try {
      const taxpayer = await lookupTaxpayerByRnc(form.fiscalId);
      setForm((current) => ({
        ...current,
        name: taxpayer.name,
        fiscalId: taxpayer.rnc || current.fiscalId,
        type: 'EMPRESARIAL',
        fiscalProfile: taxpayer.fiscalProfile ?? current.fiscalProfile
      }));
      setMessage(`RNC encontrado en ${taxpayer.source}: ${taxpayer.name}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo consultar el RNC.');
    } finally {
      setLookingUpTaxpayer(false);
    }
  };

  const toggleCustomerStatus = async (customer: Customer) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para cambiar el estado del cliente.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const updated = customer.active
        ? await deactivateCustomer(customer.id)
        : await updateCustomer(customer.id, customerToRequest(customer, true));

      setCustomers((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      if (form.id === updated.id) {
        setForm(customerToForm(updated));
      }
      setMessage(`${updated.name} quedo ${updated.active ? 'activo' : 'inactivo'}.`);
      onCustomersChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo cambiar el estado del cliente.');
    } finally {
      setSaving(false);
    }
  };

  useEffect(() => {
    loadCustomers('');
    // Solo se carga al entrar al modulo; las busquedas se controlan con el formulario.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiOnline]);

  return (
    <section className="customers-layout">
      <div className="panel customers-list-panel">
        <div className="panel-heading">
          <div>
            <h2>Clientes registrados</h2>
            <p>{customers.length} clientes encontrados, {activeCustomers} activos.</p>
          </div>
          <button className="secondary-button" onClick={() => loadCustomers('')} disabled={loading}>
            <RefreshCw size={17} />
            <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
          </button>
        </div>

        <form
          className="history-search"
          onSubmit={(event) => {
            event.preventDefault();
            loadCustomers(search);
          }}
        >
          <label>
            Buscar cliente
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Nombre, RNC o cedula"
            />
          </label>
          <button className="primary-button" type="submit">
            <Search size={17} />
            <span>Buscar</span>
          </button>
        </form>

        {message && <div className="form-message">{message}</div>}

        <div className="table-wrap customers-table">
          <table>
            <thead>
              <tr>
                <th>Cliente</th>
                <th>Tipo</th>
                <th>Perfil fiscal</th>
                <th>Telefono</th>
                <th>Credito</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((customer) => (
                <tr key={customer.id}>
                  <td>
                    <div className="customer-name-cell">
                      <strong>{customer.name}</strong>
                      <span>{customer.fiscalId || 'Sin RNC/cedula'}</span>
                    </div>
                  </td>
                  <td>{customerTypeLabels[customer.type]}</td>
                  <td>{customerFiscalProfileLabels[customer.fiscalProfile]}</td>
                  <td>{customer.phone || 'Sin telefono'}</td>
                  <td>{money.format(Number(customer.creditLimit))}</td>
                  <td>
                    <span className={customer.active ? 'status-badge active' : 'status-badge inactive'}>
                      {customer.active ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td>
                    <div className="row-actions">
                      <button className="secondary-button" onClick={() => editCustomer(customer)}>
                        <Edit3 size={16} />
                        <span>Editar</span>
                      </button>
                      <button className="secondary-button" onClick={() => toggleCustomerStatus(customer)} disabled={saving}>
                        {customer.active ? <Ban size={16} /> : <CheckCircle2 size={16} />}
                        <span>{customer.active ? 'Desactivar' : 'Activar'}</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="panel customer-form-panel">
        <div className="panel-heading compact">
          <div>
            <h2>{form.id ? 'Editar cliente' : 'Nuevo cliente'}</h2>
            <p>Datos comerciales para ventas, credito y reportes.</p>
          </div>
          <UserPlus size={22} />
        </div>

        <div className="customer-editor-form">
          <label>
            Nombre
            <input
              value={form.name}
              onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
              placeholder="Nombre o razon social"
            />
          </label>

          <label>
            Tipo
            <select
              value={form.type}
              onChange={(event) => setForm((current) => ({ ...current, type: event.target.value as CustomerType }))}
            >
              <option value="FINAL">Consumidor final</option>
              <option value="EMPRESARIAL">Empresa / credito fiscal</option>
            </select>
          </label>

          <label>
            Perfil fiscal
            <select
              value={form.fiscalProfile}
              onChange={(event) =>
                setForm((current) => ({ ...current, fiscalProfile: event.target.value as CustomerFiscalProfile }))
              }
            >
              <option value="STANDARD">Normal</option>
              <option value="TAX_CREDIT">Credito fiscal</option>
              <option value="FREE_ZONE">Zona franca</option>
              <option value="GOVERNMENT">Gubernamental</option>
              <option value="SPECIAL_REGIME">Regimen especial</option>
            </select>
          </label>

          <label>
            RNC o cedula
            <input
              value={form.fiscalId}
              onChange={(event) => setForm((current) => ({ ...current, fiscalId: event.target.value }))}
              placeholder="00112345678"
            />
          </label>

          <button
            className="secondary-button customer-lookup-button"
            onClick={fillCustomerFromTaxpayer}
            disabled={lookingUpTaxpayer}
          >
            <Search size={17} />
            <span>{lookingUpTaxpayer ? 'Consultando...' : 'Consultar RNC'}</span>
          </button>

          <label>
            Telefono
            <input
              value={form.phone}
              onChange={(event) => setForm((current) => ({ ...current, phone: event.target.value }))}
              placeholder="809-555-0000"
            />
          </label>

          <label>
            Correo
            <input
              value={form.email}
              onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
              placeholder="cliente@empresa.com"
              type="email"
            />
          </label>

          <label>
            Limite de credito
            <input
              min={0}
              type="number"
              value={form.creditLimit}
              onChange={(event) => setForm((current) => ({ ...current, creditLimit: Number(event.target.value) }))}
            />
          </label>

          <label className="span-2">
            Direccion
            <textarea
              value={form.address}
              onChange={(event) => setForm((current) => ({ ...current, address: event.target.value }))}
              placeholder="Direccion del cliente"
              rows={3}
            />
          </label>

          <label className="customer-active-toggle">
            <input
              checked={form.active}
              onChange={(event) => setForm((current) => ({ ...current, active: event.target.checked }))}
              type="checkbox"
            />
            Cliente activo
          </label>
        </div>

        <div className="customer-form-actions">
          <button className="primary-button" onClick={submitCustomer} disabled={saving}>
            <Save size={17} />
            <span>{saving ? 'Guardando...' : 'Guardar cliente'}</span>
          </button>
          <button className="secondary-button" onClick={resetForm}>
            <Plus size={17} />
            <span>Nuevo</span>
          </button>
        </div>
      </div>
    </section>
  );
}

function EmployeesView({
  apiOnline,
  onEmployeesChanged
}: {
  apiOnline: boolean;
  onEmployeesChanged: () => void;
}) {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [form, setForm] = useState<EmployeeFormState>(createBlankEmployeeForm());
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  const activeEmployees = employees.filter((employee) => employee.active).length;
  const usersWithAccess = employees.filter((employee) => employee.username).length;

  const loadEmployees = async (term = search) => {
    if (!apiOnline) {
      setEmployees([]);
      setMessage('El backend debe estar conectado para gestionar empleados.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getEmployees(term);
      setEmployees(data);
      if (data.length === 0) {
        setMessage('No hay empleados para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar los empleados.');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setForm(createBlankEmployeeForm());
    setMessage('');
  };

  const editEmployee = (employee: Employee) => {
    setForm(employeeToForm(employee));
    setMessage('');
  };

  const setPermission = (module: EmployeeAccessModule, accessLevel: EmployeeAccessLevel) => {
    setForm((current) => ({
      ...current,
      permissions: {
        ...current.permissions,
        [module]: accessLevel
      }
    }));
  };

  const applyPermissionTemplate = (template: (typeof employeePermissionTemplates)[number]) => {
    setForm((current) => ({
      ...current,
      permissions: {
        ...createDefaultEmployeePermissions(),
        ...template.permissions
      }
    }));
    setMessage(`Plantilla aplicada: ${template.label}. Revisa y guarda el empleado.`);
  };

  const applyPermissionGroup = (modulesToUpdate: EmployeeAccessModule[], accessLevel: EmployeeAccessLevel, label: string) => {
    setForm((current) => {
      const nextPermissions = { ...current.permissions };
      modulesToUpdate.forEach((module) => {
        nextPermissions[module] = accessLevel;
      });
      return {
        ...current,
        permissions: nextPermissions
      };
    });
    setMessage(`Grupo aplicado: ${label}. Puedes ajustar permisos especificos antes de guardar.`);
  };

  const submitEmployee = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar empleados.');
      return;
    }

    if (!form.firstName.trim() || !form.lastName.trim() || !form.position.trim()) {
      setMessage('Nombre, apellido y puesto son obligatorios.');
      return;
    }

    if (!form.id && form.username.trim() && !form.password.trim()) {
      setMessage('Para crear un usuario nuevo debes escribir una contraseña temporal.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const request = employeeFormToRequest(form);
      const saved = form.id
        ? await updateEmployee(form.id, request)
        : await createEmployee(request);

      setForm(employeeToForm(saved));
      setMessage(`Empleado guardado: ${saved.fullName}.`);
      await loadEmployees(search);
      onEmployeesChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar el empleado.');
    } finally {
      setSaving(false);
    }
  };

  const permissionSummary = (employee: Employee) => {
    const writeCount = employee.permissions.filter((permission) => permission.accessLevel === 'WRITE').length;
    const readCount = employee.permissions.filter((permission) => permission.accessLevel === 'READ').length;

    if (writeCount === 0 && readCount === 0) {
      return 'Sin permisos configurados';
    }

    return `${writeCount} editar, ${readCount} solo ver`;
  };

  useEffect(() => {
    loadEmployees('');
    // Solo se carga al entrar al modulo; las busquedas se controlan con el formulario.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiOnline]);

  return (
    <section className="employees-layout">
      <div className="panel employees-list-panel">
        <div className="panel-heading">
          <div>
            <h2>Empleados y accesos</h2>
            <p>{employees.length} empleados, {activeEmployees} activos, {usersWithAccess} con usuario.</p>
          </div>
          <button className="secondary-button" onClick={() => loadEmployees('')} disabled={loading}>
            <RefreshCw size={17} />
            <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
          </button>
        </div>

        <form
          className="history-search"
          onSubmit={(event) => {
            event.preventDefault();
            loadEmployees(search);
          }}
        >
          <label>
            Buscar empleado
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Nombre, cedula o apellido"
            />
          </label>
          <button className="primary-button" type="submit">
            <Search size={17} />
            <span>Buscar</span>
          </button>
        </form>

        {message && <div className="form-message">{message}</div>}

        <div className="table-wrap employees-table">
          <table>
            <thead>
              <tr>
                <th>Empleado</th>
                <th>Puesto</th>
                <th>Usuario</th>
                <th>Permisos</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {employees.map((employee) => (
                <tr key={employee.id}>
                  <td>
                    <div className="customer-name-cell">
                      <strong>{employee.fullName}</strong>
                      <span>{employee.documentId || 'Sin cedula'}</span>
                    </div>
                  </td>
                  <td>
                    <div className="customer-name-cell">
                      <strong>{employee.position}</strong>
                      <span>{employee.department || 'Sin departamento'}</span>
                    </div>
                  </td>
                  <td>{employee.username || 'Sin usuario'}</td>
                  <td>{permissionSummary(employee)}</td>
                  <td>
                    <span className={employee.active ? 'status-badge active' : 'status-badge inactive'}>
                      {employee.active ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td>
                    <div className="row-actions">
                      <button className="secondary-button" onClick={() => editEmployee(employee)}>
                        <Edit3 size={16} />
                        <span>Editar</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="panel employee-form-panel">
        <div className="panel-heading compact">
          <div>
            <h2>{form.id ? 'Editar empleado' : 'Nuevo empleado'}</h2>
            <p>Datos laborales, usuario de acceso y permisos por modulo.</p>
          </div>
          <ShieldCheck size={22} />
        </div>

        <div className="employee-editor-form">
          <div className="employee-form-section">
            <h3>Datos del empleado</h3>
            <div className="employee-form-grid">
              <label>
                Nombre
                <input
                  value={form.firstName}
                  onChange={(event) => setForm((current) => ({ ...current, firstName: event.target.value }))}
                  placeholder="Ana"
                />
              </label>
              <label>
                Apellido
                <input
                  value={form.lastName}
                  onChange={(event) => setForm((current) => ({ ...current, lastName: event.target.value }))}
                  placeholder="Peralta"
                />
              </label>
              <label>
                Cedula
                <input
                  value={form.documentId}
                  onChange={(event) => setForm((current) => ({ ...current, documentId: event.target.value }))}
                  placeholder="00100000001"
                />
              </label>
              <label>
                Puesto
                <input
                  value={form.position}
                  onChange={(event) => setForm((current) => ({ ...current, position: event.target.value }))}
                  placeholder="Cajero"
                />
              </label>
              <label>
                Departamento
                <input
                  value={form.department}
                  onChange={(event) => setForm((current) => ({ ...current, department: event.target.value }))}
                  placeholder="Ventas"
                />
              </label>
              <label>
                Fecha entrada
                <input
                  type="date"
                  value={form.hireDate}
                  onChange={(event) => setForm((current) => ({ ...current, hireDate: event.target.value }))}
                />
              </label>
              <label>
                Telefono
                <input
                  value={form.phone}
                  onChange={(event) => setForm((current) => ({ ...current, phone: event.target.value }))}
                  placeholder="809-555-0000"
                />
              </label>
              <label>
                Correo
                <input
                  value={form.email}
                  onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
                  placeholder="empleado@peraltapos.local"
                  type="email"
                />
              </label>
              <label>
                Salario
                <input
                  min={0}
                  type="number"
                  value={form.salary}
                  onChange={(event) => setForm((current) => ({ ...current, salary: Number(event.target.value) }))}
                />
              </label>
              <label>
                Comision %
                <input
                  min={0}
                  step="0.01"
                  type="number"
                  value={form.commissionRate}
                  onChange={(event) =>
                    setForm((current) => ({ ...current, commissionRate: Number(event.target.value) }))
                  }
                />
              </label>
              <label className="customer-active-toggle">
                <input
                  checked={form.active}
                  onChange={(event) => setForm((current) => ({ ...current, active: event.target.checked }))}
                  type="checkbox"
                />
                Empleado activo
              </label>
            </div>
          </div>

          <div className="employee-form-section">
            <h3>Usuario de acceso</h3>
            <p>Si dejas el usuario vacio, el empleado queda registrado sin acceso al sistema.</p>
            <div className="employee-form-grid">
              <label>
                Usuario
                <input
                  value={form.username}
                  onChange={(event) => setForm((current) => ({ ...current, username: event.target.value }))}
                  placeholder="usuario.empleado"
                />
              </label>
              <label>
                Contraseña temporal
                <input
                  value={form.password}
                  onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
                  placeholder={form.id ? 'Solo si quieres cambiarla' : 'Obligatoria si creas usuario'}
                  type="password"
                />
              </label>
              <label className="customer-active-toggle">
                <input
                  checked={form.userActive}
                  onChange={(event) => setForm((current) => ({ ...current, userActive: event.target.checked }))}
                  type="checkbox"
                />
                Usuario activo
              </label>
              <label className="customer-active-toggle">
                <input
                  checked={form.allowWebAccess}
                  onChange={(event) => setForm((current) => ({ ...current, allowWebAccess: event.target.checked }))}
                  type="checkbox"
                />
                Puede entrar por web/movil
              </label>
            </div>
          </div>

          <div className="employee-form-section">
            <h3>Permisos por modulo</h3>
            <div className="permission-template-list">
              {employeePermissionTemplates.map((template) => (
                <button
                  className="secondary-button"
                  key={template.label}
                  onClick={() => applyPermissionTemplate(template)}
                  type="button"
                >
                  <ShieldCheck size={15} />
                  <span>{template.label}</span>
                </button>
              ))}
              <button
                className="secondary-button"
                onClick={() => applyPermissionGroup(inventoryPermissionModules, 'WRITE', 'Inventario completo')}
                type="button"
              >
                <Warehouse size={15} />
                <span>Inventario completo</span>
              </button>
              <button
                className="secondary-button"
                onClick={() => applyPermissionGroup(inventoryPermissionModules, 'READ', 'Inventario solo ver')}
                type="button"
              >
                <Eye size={15} />
                <span>Inventario solo ver</span>
              </button>
            </div>
            <div className="permission-grid">
              {employeePermissionModules.map((module) => (
                <label className="permission-row" key={module.key}>
                  <span>
                    <strong>{module.label}</strong>
                    <small>{module.description}</small>
                  </span>
                  <select
                    value={form.permissions[module.key]}
                    onChange={(event) => setPermission(module.key, event.target.value as EmployeeAccessLevel)}
                  >
                    <option value="NONE">{employeeAccessLabels.NONE}</option>
                    <option value="READ">{employeeAccessLabels.READ}</option>
                    <option value="WRITE">{employeeAccessLabels.WRITE}</option>
                  </select>
                </label>
              ))}
            </div>
          </div>
        </div>

        <div className="customer-form-actions">
          <button className="primary-button" onClick={submitEmployee} disabled={saving}>
            <Save size={17} />
            <span>{saving ? 'Guardando...' : 'Guardar empleado'}</span>
          </button>
          <button className="secondary-button" onClick={resetForm}>
            <Plus size={17} />
            <span>Nuevo</span>
          </button>
        </div>
      </div>
    </section>
  );
}

function InventoryView({
  apiOnline,
  authUser,
  products,
  onInventoryChanged
}: {
  apiOnline: boolean;
  authUser: AuthUser | null;
  products: Product[];
  onInventoryChanged: () => void;
}) {
  const [activeTab, setActiveTab] = useState<InventoryTab>('products');
  const [productForm, setProductForm] = useState<ProductFormState>(createBlankProductForm());
  const [supplierForm, setSupplierForm] = useState<SupplierFormState>(createBlankSupplierForm());
  const [movementForm, setMovementForm] = useState<InventoryMovementFormState>(createBlankMovementForm());
  const [purchaseForm, setPurchaseForm] = useState<PurchaseInvoiceFormState>(createBlankPurchaseForm());
  const [purchaseOrderForm, setPurchaseOrderForm] = useState<PurchaseOrderFormState>(createBlankPurchaseOrderForm());
  const [countForm, setCountForm] = useState<InventoryCountFormState>(createBlankCountForm());
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [movements, setMovements] = useState<InventoryMovement[]>([]);
  const [purchaseInvoices, setPurchaseInvoices] = useState<PurchaseInvoice[]>([]);
  const [purchaseOrders, setPurchaseOrders] = useState<PurchaseOrder[]>([]);
  const [inventoryCounts, setInventoryCounts] = useState<InventoryCount[]>([]);
  const [productSearch, setProductSearch] = useState('');
  const [supplierSearch, setSupplierSearch] = useState('');
  const [movementSearch, setMovementSearch] = useState('');
  const [movementTypeFilter, setMovementTypeFilter] = useState<InventoryMovementType | ''>('');
  const [movementDateFrom, setMovementDateFrom] = useState('');
  const [movementDateTo, setMovementDateTo] = useState('');
  const [purchaseSearch, setPurchaseSearch] = useState('');
  const [purchaseOrderSearch, setPurchaseOrderSearch] = useState('');
  const [showCosts, setShowCosts] = useState(false);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [lookingUpSupplier, setLookingUpSupplier] = useState(false);
  const [message, setMessage] = useState('');

  const filteredProducts = useMemo(() => {
    const searchTerm = normalizeSearchText(productSearch);
    return products.filter((product) => productMatchesSearch(product, searchTerm));
  }, [products, productSearch]);
  const activeProducts = products.filter((product) => product.active);
  const lowStockProducts = activeProducts.filter((product) => product.lowStock);
  const activeSuppliers = suppliers.filter((supplier) => supplier.active);
  const lowStockCount = products.filter((product) => product.lowStock).length;
  const inventoryValue = products.reduce(
    (sum, product) => sum + Number(product.costPrice) * Number(product.currentStock),
    0
  );
  const purchaseSubtotal = purchaseForm.items.reduce(
    (sum, item) => sum + Number(item.quantity) * Number(item.unitCost),
    0
  );
  const purchaseTaxTotal = purchaseForm.items.reduce(
    (sum, item) => sum + Number(item.quantity) * Number(item.unitCost) * (Number(item.taxRate) / 100),
    0
  );
  const purchaseTotal = purchaseSubtotal + purchaseTaxTotal;
  const purchaseOrderSubtotal = purchaseOrderForm.items.reduce(
    (sum, item) => sum + Number(item.quantity) * Number(item.unitCost),
    0
  );
  const purchaseOrderTaxTotal = purchaseOrderForm.items.reduce(
    (sum, item) => sum + Number(item.quantity) * Number(item.unitCost) * (Number(item.taxRate) / 100),
    0
  );
  const purchaseOrderTotal = purchaseOrderSubtotal + purchaseOrderTaxTotal;
  const countNetDifference = countForm.items.reduce(
    (sum, item) => sum + Number(item.countedStock) - Number(item.expectedStock),
    0
  );
  const countDifferences = countForm.items.filter(
    (item) => item.productId && Number(item.countedStock) !== Number(item.expectedStock)
  ).length;
  const activeReport = inventoryReportDetails[activeTab];
  const visibleInventoryTabs = useMemo(() => {
    return inventoryTabDetails.filter((tab) => canReadPermission(authUser, inventoryTabPermissions[tab.key]));
  }, [authUser]);

  const loadSuppliers = async (term = supplierSearch) => {
    if (!apiOnline) {
      setSuppliers([]);
      setMessage('El backend debe estar conectado para gestionar suplidores.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getSuppliers(term);
      setSuppliers(data);
      if (data.length === 0 && activeTab === 'suppliers') {
        setMessage('No hay suplidores para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar los suplidores.');
    } finally {
      setLoading(false);
    }
  };

  const loadMovements = async (term = movementSearch) => {
    if (!apiOnline) {
      setMovements([]);
      setMessage('El backend debe estar conectado para ver movimientos de inventario.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getInventoryMovements({
        search: term,
        movementType: movementTypeFilter,
        dateFrom: movementDateFrom,
        dateTo: movementDateTo
      });
      setMovements(data);
      if (data.length === 0 && activeTab === 'movements') {
        setMessage('No hay movimientos para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar los movimientos.');
    } finally {
      setLoading(false);
    }
  };

  const loadInventoryCounts = async () => {
    if (!apiOnline) {
      setInventoryCounts([]);
      setMessage('El backend debe estar conectado para gestionar conteos fisicos.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getInventoryCounts();
      setInventoryCounts(data);
      if (data.length === 0 && activeTab === 'counts') {
        setMessage('No hay conteos fisicos registrados.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar los conteos fisicos.');
    } finally {
      setLoading(false);
    }
  };

  const loadPurchaseInvoices = async (term = purchaseSearch) => {
    if (!apiOnline) {
      setPurchaseInvoices([]);
      setMessage('El backend debe estar conectado para gestionar facturas de compra.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getPurchaseInvoices(term);
      setPurchaseInvoices(data);
      if (data.length === 0 && activeTab === 'purchases') {
        setMessage('No hay facturas de compra para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar las facturas de compra.');
    } finally {
      setLoading(false);
    }
  };

  const loadPurchaseOrders = async (term = purchaseOrderSearch) => {
    if (!apiOnline) {
      setPurchaseOrders([]);
      setMessage('El backend debe estar conectado para gestionar ordenes de compra.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getPurchaseOrders(term);
      setPurchaseOrders(data);
      if (data.length === 0 && activeTab === 'replenishment') {
        setMessage('No hay ordenes de compra para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar las ordenes de compra.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (canReadPermission(authUser, 'SUPPLIERS')) {
      loadSuppliers('');
    }
    if (canReadPermission(authUser, 'MOVEMENTS')) {
      loadMovements('');
    }
    if (canReadPermission(authUser, 'PURCHASES')) {
      loadPurchaseInvoices('');
    }
    if (canReadPermission(authUser, 'REPLENISHMENT')) {
      loadPurchaseOrders('');
    }
    if (canReadPermission(authUser, 'COUNTS')) {
      loadInventoryCounts();
    }
    // Solo se carga al entrar al modulo; las busquedas se controlan con cada formulario.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiOnline, authUser]);

  useEffect(() => {
    if (visibleInventoryTabs.length > 0 && !visibleInventoryTabs.some((tab) => tab.key === activeTab)) {
      setActiveTab(visibleInventoryTabs[0].key);
    }
  }, [activeTab, visibleInventoryTabs]);

  const editProduct = (product: Product) => {
    setProductForm(productToForm(product));
    setActiveTab('products');
    setMessage('');
  };

  const submitProduct = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar productos.');
      return;
    }

    if (!productForm.sku.trim() || !productForm.name.trim()) {
      setMessage('El codigo y el nombre del producto son obligatorios.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const wasEditing = Boolean(productForm.id);
      const request = productFormToRequest(productForm);
      const saved = productForm.id
        ? await updateProduct(productForm.id, request)
        : await createProduct(request);

      await onInventoryChanged();
      setProductSearch('');
      setProductForm(wasEditing ? productToForm(saved) : createBlankProductForm());
      setMessage(`Producto guardado: ${saved.name}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar el producto.');
    } finally {
      setSaving(false);
    }
  };

  const submitMovement = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para registrar movimientos.');
      return;
    }

    if (!movementForm.productId) {
      setMessage('Selecciona un producto para el movimiento.');
      return;
    }

    if (Number(movementForm.quantity) <= 0) {
      setMessage('La cantidad debe ser mayor que cero.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const movement = await createInventoryMovement(movementFormToRequest(movementForm));
      setMovementForm(createBlankMovementForm());
      setActiveTab('movements');
      setMessage(`Movimiento registrado para ${movement.productName}.`);
      await loadMovements('');
      onInventoryChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo registrar el movimiento.');
    } finally {
      setSaving(false);
    }
  };

  const updatePurchaseLine = (key: string, changes: Partial<PurchaseLineFormState>) => {
    setPurchaseForm((current) => ({
      ...current,
      items: current.items.map((item) => (item.key === key ? { ...item, ...changes } : item))
    }));
  };

  const selectPurchaseProduct = (key: string, productId: string) => {
    const product = products.find((item) => item.id === productId);
    updatePurchaseLine(key, {
      productId,
      unitCost: product ? Number(product.costPrice) : 0,
      taxRate: product ? Number(product.taxRate) : 18
    });
  };

  const submitPurchaseInvoice = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para registrar facturas de compra.');
      return;
    }

    if (!purchaseForm.supplierId || !purchaseForm.documentNumber.trim()) {
      setMessage('Selecciona el suplidor y escribe el numero de factura.');
      return;
    }

    if (purchaseForm.items.some((item) => !item.productId || Number(item.quantity) <= 0)) {
      setMessage('Cada linea necesita un producto y una cantidad mayor que cero.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const saved = await createPurchaseInvoice(purchaseFormToRequest(purchaseForm));
      setPurchaseForm(createBlankPurchaseForm());
      setMessage(`Factura de compra registrada: ${saved.documentNumber} por ${money.format(Number(saved.total))}.`);
      await Promise.all([loadPurchaseInvoices(''), loadPurchaseOrders(''), loadMovements('')]);
      onInventoryChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo registrar la factura de compra.');
    } finally {
      setSaving(false);
    }
  };

  const updatePurchaseOrderLine = (key: string, changes: Partial<PurchaseLineFormState>) => {
    setPurchaseOrderForm((current) => ({
      ...current,
      items: current.items.map((item) => (item.key === key ? { ...item, ...changes } : item))
    }));
  };

  const selectPurchaseOrderProduct = (key: string, productId: string) => {
    const product = products.find((item) => item.id === productId);
    updatePurchaseOrderLine(key, {
      productId,
      unitCost: product ? Number(product.costPrice) : 0,
      taxRate: product ? Number(product.taxRate) : 18
    });
  };

  const loadLowStockIntoOrder = () => {
    if (lowStockProducts.length === 0) {
      setMessage('No hay productos activos con stock bajo.');
      return;
    }

    setPurchaseOrderForm((current) => ({
      ...current,
      items: lowStockProducts.map((product) => {
        const targetStock = Number(product.minimumStock) > 0 ? Number(product.minimumStock) * 2 : 1;
        return {
          key: `${product.id}-${Date.now()}`,
          productId: product.id,
          quantity: Math.max(targetStock - Number(product.currentStock), 1),
          unitCost: Number(product.costPrice),
          taxRate: Number(product.taxRate)
        };
      })
    }));
    setMessage(`${lowStockProducts.length} productos con stock bajo agregados a la orden.`);
  };

  const submitPurchaseOrder = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para crear ordenes de compra.');
      return;
    }

    if (!purchaseOrderForm.supplierId) {
      setMessage('Selecciona un suplidor para la orden.');
      return;
    }

    if (purchaseOrderForm.items.some((item) => !item.productId || Number(item.quantity) <= 0)) {
      setMessage('Cada linea necesita un producto y una cantidad mayor que cero.');
      return;
    }

    if (new Set(purchaseOrderForm.items.map((item) => item.productId)).size !== purchaseOrderForm.items.length) {
      setMessage('Un producto no puede repetirse en la misma orden.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const saved = await createPurchaseOrder(purchaseOrderFormToRequest(purchaseOrderForm));
      setPurchaseOrderForm(createBlankPurchaseOrderForm());
      setMessage(`Orden creada: ${saved.orderNumber} por ${money.format(Number(saved.total))}.`);
      await loadPurchaseOrders('');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo crear la orden de compra.');
    } finally {
      setSaving(false);
    }
  };

  const receivePurchaseOrder = (order: PurchaseOrder) => {
    setPurchaseForm({
      purchaseOrderId: order.id,
      supplierId: order.supplierId,
      documentNumber: '',
      invoiceDate: new Date().toISOString().slice(0, 10),
      dueDate: '',
      paymentTerm: 'CASH',
      notes: `Recepcion de ${order.orderNumber}`,
      items: order.items.map((item) => ({
        key: `${item.id}-${Date.now()}`,
        productId: item.productId,
        quantity: Number(item.quantity),
        unitCost: Number(item.unitCost),
        taxRate: Number(item.taxRate)
      }))
    });
    setActiveTab('purchases');
    setMessage(`Completa el numero de factura para recibir ${order.orderNumber}.`);
  };

  const cancelOrder = async (order: PurchaseOrder) => {
    setSaving(true);
    setMessage('');

    try {
      const updated = await cancelPurchaseOrder(order.id);
      setPurchaseOrders((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      setMessage(`${updated.orderNumber} fue cancelada.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo cancelar la orden.');
    } finally {
      setSaving(false);
    }
  };

  const exportInventoryReport = async (format: ReportFormat) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para generar reportes.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const blob = await downloadInventoryReport(activeTab, format, showCosts);
      downloadBlob(blob, `${activeReport.filename}-${new Date().toISOString().slice(0, 10)}.${format}`);
      setMessage(`${activeReport.title} generado en ${format.toUpperCase()}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo generar el reporte.');
    } finally {
      setLoading(false);
    }
  };

  const exportOrderReport = async (order: PurchaseOrder, format: ReportFormat) => {
    setLoading(true);
    setMessage('');

    try {
      const blob = await downloadPurchaseOrderReport(order.id, format);
      downloadBlob(blob, `${order.orderNumber}.${format}`);
      setMessage(`${order.orderNumber} descargada en ${format.toUpperCase()}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo generar la orden.');
    } finally {
      setLoading(false);
    }
  };

  const printOrder = async (order: PurchaseOrder) => {
    const printWindow = window.open('', '_blank');
    if (printWindow) {
      printWindow.opener = null;
      printWindow.document.title = `Preparando ${order.orderNumber}`;
      printWindow.document.body.innerHTML = '<p style="font-family: sans-serif; padding: 24px;">Preparando documento para imprimir...</p>';
    }

    setLoading(true);
    setMessage('');

    try {
      const blob = await downloadPurchaseOrderReport(order.id, 'pdf', true);
      const url = URL.createObjectURL(blob);
      if (!printWindow) {
        downloadBlob(blob, `${order.orderNumber}.pdf`);
        setMessage('El navegador bloqueo la ventana; se descargo el PDF para imprimir.');
      } else {
        printWindow.location.href = url;
        setMessage(`${order.orderNumber} abierta para imprimir.`);
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      }
    } catch (error) {
      printWindow?.close();
      setMessage(error instanceof Error ? error.message : 'No se pudo abrir la orden para imprimir.');
    } finally {
      setLoading(false);
    }
  };

  const updateCountLine = (key: string, changes: Partial<InventoryCountLineFormState>) => {
    setCountForm((current) => ({
      ...current,
      items: current.items.map((item) => (item.key === key ? { ...item, ...changes } : item))
    }));
  };

  const selectCountProduct = (key: string, productId: string) => {
    const product = products.find((item) => item.id === productId);
    updateCountLine(key, {
      productId,
      expectedStock: product ? Number(product.currentStock) : 0,
      countedStock: product ? Number(product.currentStock) : 0
    });
  };

  const submitInventoryCount = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para registrar conteos.');
      return;
    }

    if (countForm.items.some((item) => !item.productId || Number(item.countedStock) < 0)) {
      setMessage('Cada linea necesita un producto y una existencia contada valida.');
      return;
    }

    if (new Set(countForm.items.map((item) => item.productId)).size !== countForm.items.length) {
      setMessage('Un producto no puede repetirse en el mismo conteo.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const saved = await createInventoryCount(countFormToRequest(countForm));
      setCountForm(createBlankCountForm());
      setMessage(
        `${saved.countNumber} registrado: ${saved.productsWithDifference} diferencias, balance ${formatSignedQuantity(saved.netDifference)}.`
      );
      await Promise.all([loadInventoryCounts(), loadMovements('')]);
      onInventoryChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo registrar el conteo fisico.');
    } finally {
      setSaving(false);
    }
  };

  const editSupplier = (supplier: Supplier) => {
    setSupplierForm(supplierToForm(supplier));
    setActiveTab('suppliers');
    setMessage('');
  };

  const submitSupplier = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar suplidores.');
      return;
    }

    if (!supplierForm.name.trim()) {
      setMessage('El nombre del suplidor es obligatorio.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const request = supplierFormToRequest(supplierForm);
      const saved = supplierForm.id
        ? await updateSupplier(supplierForm.id, request)
        : await createSupplier(request);

      setSupplierForm(supplierToForm(saved));
      setMessage(`Suplidor guardado: ${saved.name}.`);
      await loadSuppliers(supplierSearch);
      onInventoryChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar el suplidor.');
    } finally {
      setSaving(false);
    }
  };

  const toggleSupplierStatus = async (supplier: Supplier) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para cambiar el estado del suplidor.');
      return;
    }

    setSaving(true);
    setMessage('');

    try {
      const updated = supplier.active
        ? await deactivateSupplier(supplier.id)
        : await updateSupplier(supplier.id, supplierToRequest(supplier, true));

      setSuppliers((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      if (supplierForm.id === updated.id) {
        setSupplierForm(supplierToForm(updated));
      }
      setMessage(`${updated.name} quedo ${updated.active ? 'activo' : 'inactivo'}.`);
      onInventoryChanged();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo cambiar el estado del suplidor.');
    } finally {
      setSaving(false);
    }
  };

  const fillSupplierFromTaxpayer = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para consultar RNC.');
      return;
    }

    if (!supplierForm.rnc.trim()) {
      setMessage('Escribe el RNC del suplidor antes de consultar.');
      return;
    }

    setLookingUpSupplier(true);
    setMessage('');

    try {
      const taxpayer = await lookupTaxpayerByRnc(supplierForm.rnc);
      setSupplierForm((current) => ({
        ...current,
        name: taxpayer.name,
        rnc: taxpayer.rnc || current.rnc
      }));
      setMessage(`RNC encontrado en ${taxpayer.source}: ${taxpayer.name}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo consultar el RNC.');
    } finally {
      setLookingUpSupplier(false);
    }
  };

  return (
    <section className="inventory-layout">
      <div className="inventory-kpi-grid">
        <MetricCard label="Productos activos" value={activeProducts.length.toString()} tone="green" />
        <MetricCard label="Stock bajo" value={lowStockCount.toString()} tone="red" />
        <MetricCard label="Valor al costo" value={showCosts ? money.format(inventoryValue) : 'Oculto'} tone="blue" />
        <MetricCard label="Suplidores activos" value={activeSuppliers.length.toString()} tone="sand" />
      </div>

      <div className="inventory-tabs" role="tablist" aria-label="Secciones de inventario">
        {visibleInventoryTabs.map((tab) => {
          const Icon = tab.icon;
          return (
            <button
              key={tab.key}
              className={activeTab === tab.key ? 'inventory-tab active' : 'inventory-tab'}
              onClick={() => setActiveTab(tab.key)}
            >
              <Icon size={17} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      <div className={`inventory-report-bar${activeReport.supportsCosts ? '' : ' no-cost-toggle'}`}>
        <div>
          <strong>{activeReport.title}</strong>
          <span>{activeReport.description}</span>
        </div>
        {activeReport.supportsCosts && (
          <label className="report-cost-toggle">
            <input checked={showCosts} onChange={(event) => setShowCosts(event.target.checked)} type="checkbox" />
            Incluir costos
          </label>
        )}
        <button
          className="secondary-button"
          onClick={() => exportInventoryReport('xlsx')}
          disabled={loading || !canReadPermission(authUser, 'REPORTS')}
        >
          <FileSpreadsheet size={17} />
          <span>Excel</span>
        </button>
        <button
          className="secondary-button"
          onClick={() => exportInventoryReport('pdf')}
          disabled={loading || !canReadPermission(authUser, 'REPORTS')}
        >
          <Download size={17} />
          <span>PDF</span>
        </button>
        <button
          className="secondary-button"
          onClick={() => exportInventoryReport('docx')}
          disabled={loading || !canReadPermission(authUser, 'REPORTS')}
        >
          <FileText size={17} />
          <span>Word</span>
        </button>
      </div>

      {message && <div className="form-message inventory-message">{message}</div>}

      {activeTab === 'products' && (
        <div className="inventory-two-column">
          <div className="panel inventory-list-panel">
            <div className="panel-heading">
              <div>
                <h2>Catalogo de productos</h2>
                <p>{filteredProducts.length} productos encontrados.</p>
              </div>
              <button className="secondary-button" onClick={onInventoryChanged}>
                <RefreshCw size={17} />
                <span>Actualizar</span>
              </button>
            </div>

            <label className="product-search inventory-search">
              Buscar producto
              <div>
                <Search size={17} />
                <input
                  value={productSearch}
                  onChange={(event) => setProductSearch(event.target.value)}
                  placeholder="Nombre, SKU, codigo, categoria o marca"
                />
              </div>
            </label>

            <div className="table-wrap inventory-table">
              <table>
                <thead>
                  <tr>
                    <th>Codigo</th>
                    <th>Producto</th>
                    <th>Stock</th>
                    {showCosts && <th>Costo</th>}
                    <th>Precio</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredProducts.map((product) => (
                    <tr key={product.id}>
                      <td>{product.sku}</td>
                      <td>
                        <div className="customer-name-cell">
                          <strong>{product.name}</strong>
                          <span>
                            {product.categoryName || 'Sin categoria'}
                            {product.brandName ? ` - ${product.brandName}` : ''}
                          </span>
                        </div>
                      </td>
                      <td>
                        <span className={product.lowStock ? 'stock low' : 'stock'}>
                          {formatQuantity(product.currentStock)}
                        </span>
                      </td>
                      {showCosts && <td>{money.format(Number(product.costPrice))}</td>}
                      <td>{money.format(Number(product.salePrice))}</td>
                      <td>
                        <span className={product.active ? 'status-badge active' : 'status-badge inactive'}>
                          {product.active ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td>
                        <button className="secondary-button" onClick={() => editProduct(product)}>
                          <Edit3 size={16} />
                          <span>Editar</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="panel inventory-form-panel">
            <div className="panel-heading compact">
              <div>
                <h2>{productForm.id ? 'Editar producto' : 'Nuevo producto'}</h2>
                <p>Catalogo, precios e impuestos del articulo.</p>
              </div>
              <Package size={22} />
            </div>

            <form
              className="customer-editor-form"
              onSubmit={(event) => {
                event.preventDefault();
                submitProduct();
              }}
            >
              <label>
                Codigo interno
                <input
                  value={productForm.sku}
                  onChange={(event) => setProductForm((current) => ({ ...current, sku: event.target.value }))}
                  placeholder="FT-001"
                />
              </label>
              <label>
                Codigo de barra
                <input
                  value={productForm.barcode}
                  onChange={(event) => setProductForm((current) => ({ ...current, barcode: event.target.value }))}
                  placeholder="Opcional"
                />
              </label>
              <label className="span-2">
                Nombre
                <input
                  value={productForm.name}
                  onChange={(event) => setProductForm((current) => ({ ...current, name: event.target.value }))}
                  placeholder="Nombre del producto"
                />
              </label>
              <label>
                Categoria
                <input
                  value={productForm.categoryName}
                  onChange={(event) => setProductForm((current) => ({ ...current, categoryName: event.target.value }))}
                  placeholder="Ferreteria"
                />
              </label>
              <label>
                Marca
                <input
                  value={productForm.brandName}
                  onChange={(event) => setProductForm((current) => ({ ...current, brandName: event.target.value }))}
                  placeholder="Marca"
                />
              </label>
              <label>
                Unidad
                <input
                  value={productForm.unit}
                  onChange={(event) => setProductForm((current) => ({ ...current, unit: event.target.value }))}
                  placeholder="unidad, caja, saco"
                />
              </label>
              <label>
                ITBIS %
                <input
                  min={0}
                  type="number"
                  value={productForm.taxRate}
                  onChange={(event) => setProductForm((current) => ({ ...current, taxRate: Number(event.target.value) }))}
                />
              </label>
              <label>
                Costo
                <input
                  min={0}
                  type="number"
                  value={productForm.costPrice}
                  onChange={(event) => setProductForm((current) => ({ ...current, costPrice: Number(event.target.value) }))}
                />
              </label>
              <label>
                Precio venta
                <input
                  min={0}
                  type="number"
                  value={productForm.salePrice}
                  onChange={(event) => setProductForm((current) => ({ ...current, salePrice: Number(event.target.value) }))}
                />
              </label>
              <label>
                Stock inicial
                <input
                  disabled={Boolean(productForm.id)}
                  min={0}
                  type="number"
                  value={productForm.currentStock}
                  onChange={(event) => setProductForm((current) => ({ ...current, currentStock: Number(event.target.value) }))}
                />
              </label>
              <label>
                Stock minimo
                <input
                  min={0}
                  type="number"
                  value={productForm.minimumStock}
                  onChange={(event) => setProductForm((current) => ({ ...current, minimumStock: Number(event.target.value) }))}
                />
              </label>
              {productForm.id && (
                <div className="field-note span-2">
                  Los cambios de existencia se registran desde Movimientos para conservar historial.
                </div>
              )}
              <label className="span-2">
                Descripcion
                <textarea
                  value={productForm.description}
                  onChange={(event) => setProductForm((current) => ({ ...current, description: event.target.value }))}
                  placeholder="Detalle interno del producto"
                  rows={3}
                />
              </label>
              <label className="customer-active-toggle">
                <input
                  checked={productForm.active}
                  onChange={(event) => setProductForm((current) => ({ ...current, active: event.target.checked }))}
                  type="checkbox"
                />
                Producto activo
              </label>
              <div className="customer-form-actions span-2">
                <button className="primary-button" type="submit" disabled={saving}>
                  <Save size={17} />
                  <span>{saving ? 'Guardando...' : 'Guardar producto'}</span>
                </button>
                <button className="secondary-button" type="button" onClick={() => setProductForm(createBlankProductForm())}>
                  <Plus size={17} />
                  <span>Nuevo</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {activeTab === 'replenishment' && (
        <div className="replenishment-layout">
          <div className="panel replenishment-entry-panel">
            <div className="panel-heading">
              <div>
                <h2>Nueva orden de compra</h2>
                <p>Prepara la reposicion sin modificar el inventario hasta recibir la factura.</p>
              </div>
              <ShoppingCart size={22} />
            </div>

            <div className="reorder-alert">
              <div>
                <strong>{lowStockProducts.length} productos necesitan reposicion</strong>
                <span>La cantidad sugerida lleva cada producto al doble de su stock minimo.</span>
              </div>
              <button className="secondary-button" type="button" onClick={loadLowStockIntoOrder}>
                <Package size={17} />
                <span>Cargar stock bajo</span>
              </button>
            </div>

            <div className="reorder-products">
              {lowStockProducts.slice(0, 6).map((product) => (
                <div className="reorder-product-row" key={product.id}>
                  <div>
                    <strong>{product.name}</strong>
                    <span>{product.sku}</span>
                  </div>
                  <div>
                    <small>Actual</small>
                    <strong className="negative">{formatQuantity(product.currentStock)}</strong>
                  </div>
                  <div>
                    <small>Minimo</small>
                    <strong>{formatQuantity(product.minimumStock)}</strong>
                  </div>
                </div>
              ))}
              {lowStockProducts.length === 0 && (
                <div className="field-note">No hay productos activos con stock bajo.</div>
              )}
            </div>

            <div className="purchase-header-form reorder-header-form">
              <label>
                Suplidor
                <select
                  value={purchaseOrderForm.supplierId}
                  onChange={(event) =>
                    setPurchaseOrderForm((current) => ({ ...current, supplierId: event.target.value }))
                  }
                >
                  <option value="">Selecciona un suplidor</option>
                  {activeSuppliers.map((supplier) => (
                    <option key={supplier.id} value={supplier.id}>
                      {supplier.name}{supplier.rnc ? ` - ${supplier.rnc}` : ''}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Fecha esperada
                <input
                  type="date"
                  value={purchaseOrderForm.expectedDate}
                  onChange={(event) =>
                    setPurchaseOrderForm((current) => ({ ...current, expectedDate: event.target.value }))
                  }
                />
              </label>
            </div>

            <div className="purchase-lines-heading">
              <div>
                <h3>Productos solicitados</h3>
                <span>Los costos son estimados y pueden corregirse al recibir la factura.</span>
              </div>
              <button
                className="secondary-button"
                type="button"
                onClick={() =>
                  setPurchaseOrderForm((current) => ({
                    ...current,
                    items: [...current.items, createBlankPurchaseLine()]
                  }))
                }
              >
                <Plus size={17} />
                <span>Agregar linea</span>
              </button>
            </div>

            <div className="purchase-lines">
              {purchaseOrderForm.items.map((item, index) => {
                const lineSubtotal = Number(item.quantity) * Number(item.unitCost);
                const lineTotal = lineSubtotal + lineSubtotal * (Number(item.taxRate) / 100);

                return (
                  <article className="purchase-line" key={item.key}>
                    <label className="purchase-product-field">
                      Producto
                      <select
                        value={item.productId}
                        onChange={(event) => selectPurchaseOrderProduct(item.key, event.target.value)}
                      >
                        <option value="">Selecciona un producto</option>
                        {activeProducts.map((product) => (
                          <option key={product.id} value={product.id}>
                            {product.sku} - {product.name}
                          </option>
                        ))}
                      </select>
                    </label>
                    <label>
                      Cantidad
                      <input
                        min={0.01}
                        step={0.01}
                        type="number"
                        value={item.quantity}
                        onChange={(event) =>
                          updatePurchaseOrderLine(item.key, { quantity: Number(event.target.value) })
                        }
                      />
                    </label>
                    <label>
                      Costo estimado
                      <input
                        min={0}
                        step={0.01}
                        type="number"
                        value={item.unitCost}
                        onChange={(event) =>
                          updatePurchaseOrderLine(item.key, { unitCost: Number(event.target.value) })
                        }
                      />
                    </label>
                    <label>
                      ITBIS %
                      <input
                        min={0}
                        step={0.01}
                        type="number"
                        value={item.taxRate}
                        onChange={(event) =>
                          updatePurchaseOrderLine(item.key, { taxRate: Number(event.target.value) })
                        }
                      />
                    </label>
                    <div className="purchase-line-total">
                      <span>Total linea</span>
                      <strong>{money.format(lineTotal)}</strong>
                    </div>
                    <button
                      className="icon-button"
                      type="button"
                      title={`Quitar linea ${index + 1}`}
                      disabled={purchaseOrderForm.items.length === 1}
                      onClick={() =>
                        setPurchaseOrderForm((current) => ({
                          ...current,
                          items: current.items.filter((line) => line.key !== item.key)
                        }))
                      }
                    >
                      <Trash2 size={17} />
                    </button>
                  </article>
                );
              })}
            </div>

            <div className="purchase-footer">
              <label>
                Nota
                <textarea
                  value={purchaseOrderForm.notes}
                  onChange={(event) =>
                    setPurchaseOrderForm((current) => ({ ...current, notes: event.target.value }))
                  }
                  placeholder="Condiciones, contacto o instrucciones al suplidor"
                  rows={3}
                />
              </label>
              <div className="purchase-totals">
                <div><span>Subtotal</span><strong>{money.format(purchaseOrderSubtotal)}</strong></div>
                <div><span>ITBIS estimado</span><strong>{money.format(purchaseOrderTaxTotal)}</strong></div>
                <div className="purchase-total"><span>Total estimado</span><strong>{money.format(purchaseOrderTotal)}</strong></div>
              </div>
            </div>

            <div className="purchase-actions">
              <button className="primary-button" onClick={submitPurchaseOrder} disabled={saving}>
                <Save size={17} />
                <span>{saving ? 'Creando...' : 'Crear orden de compra'}</span>
              </button>
              <button
                className="secondary-button"
                onClick={() => setPurchaseOrderForm(createBlankPurchaseOrderForm())}
              >
                <Plus size={17} />
                <span>Nueva orden</span>
              </button>
            </div>
          </div>

          <div className="panel purchase-order-history-panel">
            <div className="panel-heading">
              <div>
                <h2>Ordenes de compra</h2>
                <p>Pendientes, recibidas y canceladas.</p>
              </div>
              <button className="secondary-button" onClick={() => loadPurchaseOrders('')} disabled={loading}>
                <RefreshCw size={17} />
                <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
              </button>
            </div>

            <form
              className="history-search"
              onSubmit={(event) => {
                event.preventDefault();
                loadPurchaseOrders(purchaseOrderSearch);
              }}
            >
              <label>
                Buscar orden
                <input
                  value={purchaseOrderSearch}
                  onChange={(event) => setPurchaseOrderSearch(event.target.value)}
                  placeholder="Numero o suplidor"
                />
              </label>
              <button className="primary-button" type="submit">
                <Search size={17} />
                <span>Buscar</span>
              </button>
            </form>

            <div className="purchase-order-list">
              {purchaseOrders.map((order) => (
                <article className="purchase-order-row" key={order.id}>
                  <div className="purchase-order-heading">
                    <div>
                      <strong>{order.orderNumber}</strong>
                      <span>{order.supplierName}</span>
                    </div>
                    <span className={`order-status ${order.status.toLowerCase()}`}>
                      {purchaseOrderStatusLabels[order.status]}
                    </span>
                  </div>
                  <div className="purchase-order-meta">
                    <span>{new Date(`${order.orderDate}T00:00:00`).toLocaleDateString('es-DO')}</span>
                    <span>{order.items.length} productos</span>
                    <strong>{money.format(Number(order.total))}</strong>
                  </div>
                  {order.status === 'OPEN' && (
                    <div className="purchase-order-actions">
                      <button className="primary-button" onClick={() => receivePurchaseOrder(order)}>
                        <ReceiptText size={16} />
                        <span>Recibir</span>
                      </button>
                      <button className="secondary-button" onClick={() => cancelOrder(order)} disabled={saving}>
                        <Ban size={16} />
                        <span>Cancelar</span>
                      </button>
                    </div>
                  )}
                  <div className="purchase-order-documents">
                    <button className="secondary-button" onClick={() => printOrder(order)} disabled={loading}>
                      <Printer size={16} />
                      <span>Imprimir</span>
                    </button>
                    <button className="secondary-button" onClick={() => exportOrderReport(order, 'pdf')} disabled={loading}>
                      <Download size={16} />
                      <span>PDF</span>
                    </button>
                    <button className="secondary-button" onClick={() => exportOrderReport(order, 'xlsx')} disabled={loading}>
                      <FileSpreadsheet size={16} />
                      <span>Excel</span>
                    </button>
                    <button className="secondary-button" onClick={() => exportOrderReport(order, 'docx')} disabled={loading}>
                      <FileText size={16} />
                      <span>Word</span>
                    </button>
                  </div>
                </article>
              ))}
              {purchaseOrders.length === 0 && (
                <div className="empty-cart">Todavia no hay ordenes de compra.</div>
              )}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'purchases' && (
        <div className="purchase-layout">
          <div className="panel purchase-entry-panel">
            <div className="panel-heading">
              <div>
                <h2>Registrar factura de compra</h2>
                <p>Recibe mercancía y genera la cuenta por pagar cuando la compra es a credito.</p>
              </div>
              <ReceiptText size={22} />
            </div>

            {purchaseForm.purchaseOrderId && (
              <div className="field-note purchase-order-link-note">
                Recepcion vinculada a una orden de compra. Revisa costos y cantidades antes de registrar la factura.
              </div>
            )}

            <form
              onSubmit={(event) => {
                event.preventDefault();
                submitPurchaseInvoice();
              }}
            >
              <div className="purchase-header-form">
                <label>
                  Suplidor
                  <select
                    value={purchaseForm.supplierId}
                    onChange={(event) => setPurchaseForm((current) => ({ ...current, supplierId: event.target.value }))}
                  >
                    <option value="">Selecciona un suplidor</option>
                    {activeSuppliers.map((supplier) => (
                      <option key={supplier.id} value={supplier.id}>
                        {supplier.name}{supplier.rnc ? ` - ${supplier.rnc}` : ''}
                      </option>
                    ))}
                  </select>
                </label>
                <label>
                  Numero de factura
                  <input
                    value={purchaseForm.documentNumber}
                    onChange={(event) => setPurchaseForm((current) => ({ ...current, documentNumber: event.target.value }))}
                    placeholder="Factura o NCF del suplidor"
                  />
                </label>
                <label>
                  Fecha
                  <input
                    type="date"
                    value={purchaseForm.invoiceDate}
                    onChange={(event) => setPurchaseForm((current) => ({ ...current, invoiceDate: event.target.value }))}
                  />
                </label>
                <label>
                  Condicion
                  <select
                    value={purchaseForm.paymentTerm}
                    onChange={(event) =>
                      setPurchaseForm((current) => ({
                        ...current,
                        paymentTerm: event.target.value as PurchasePaymentTerm,
                        dueDate: event.target.value === 'CASH' ? '' : current.dueDate
                      }))
                    }
                  >
                    <option value="CASH">Contado</option>
                    <option value="CREDIT">Credito</option>
                  </select>
                </label>
                {purchaseForm.paymentTerm === 'CREDIT' && (
                  <label>
                    Vencimiento
                    <input
                      type="date"
                      value={purchaseForm.dueDate}
                      onChange={(event) => setPurchaseForm((current) => ({ ...current, dueDate: event.target.value }))}
                    />
                  </label>
                )}
              </div>

              <div className="purchase-lines-heading">
                <div>
                  <h3>Productos recibidos</h3>
                  <span>El costo registrado actualiza el ultimo costo del producto.</span>
                </div>
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() =>
                    setPurchaseForm((current) => ({
                      ...current,
                      items: [...current.items, createBlankPurchaseLine()]
                    }))
                  }
                >
                  <Plus size={17} />
                  <span>Agregar linea</span>
                </button>
              </div>

              <div className="purchase-lines">
                {purchaseForm.items.map((item, index) => {
                  const lineSubtotal = Number(item.quantity) * Number(item.unitCost);
                  const lineTotal = lineSubtotal + lineSubtotal * (Number(item.taxRate) / 100);

                  return (
                    <article className="purchase-line" key={item.key}>
                      <label className="purchase-product-field">
                        Producto
                        <select value={item.productId} onChange={(event) => selectPurchaseProduct(item.key, event.target.value)}>
                          <option value="">Selecciona un producto</option>
                          {activeProducts.map((product) => (
                            <option key={product.id} value={product.id}>
                              {product.sku} - {product.name}
                            </option>
                          ))}
                        </select>
                      </label>
                      <label>
                        Cantidad
                        <input
                          min={0.01}
                          step={0.01}
                          type="number"
                          value={item.quantity}
                          onChange={(event) => updatePurchaseLine(item.key, { quantity: Number(event.target.value) })}
                        />
                      </label>
                      <label>
                        Costo
                        <input
                          min={0}
                          step={0.01}
                          type="number"
                          value={item.unitCost}
                          onChange={(event) => updatePurchaseLine(item.key, { unitCost: Number(event.target.value) })}
                        />
                      </label>
                      <label>
                        ITBIS %
                        <input
                          min={0}
                          step={0.01}
                          type="number"
                          value={item.taxRate}
                          onChange={(event) => updatePurchaseLine(item.key, { taxRate: Number(event.target.value) })}
                        />
                      </label>
                      <div className="purchase-line-total">
                        <span>Total linea</span>
                        <strong>{money.format(lineTotal)}</strong>
                      </div>
                      <button
                        className="icon-button"
                        type="button"
                        title={`Quitar linea ${index + 1}`}
                        disabled={purchaseForm.items.length === 1}
                        onClick={() =>
                          setPurchaseForm((current) => ({
                            ...current,
                            items: current.items.filter((line) => line.key !== item.key)
                          }))
                        }
                      >
                        <Trash2 size={17} />
                      </button>
                    </article>
                  );
                })}
              </div>

              <div className="purchase-footer">
                <label>
                  Nota
                  <textarea
                    value={purchaseForm.notes}
                    onChange={(event) => setPurchaseForm((current) => ({ ...current, notes: event.target.value }))}
                    placeholder="Observacion de recepcion o condiciones"
                    rows={3}
                  />
                </label>
                <div className="purchase-totals">
                  <div><span>Subtotal</span><strong>{money.format(purchaseSubtotal)}</strong></div>
                  <div><span>ITBIS</span><strong>{money.format(purchaseTaxTotal)}</strong></div>
                  <div className="purchase-total"><span>Total</span><strong>{money.format(purchaseTotal)}</strong></div>
                </div>
              </div>

              <div className="purchase-actions">
                <button className="primary-button" type="submit" disabled={saving}>
                  <Save size={17} />
                  <span>{saving ? 'Registrando...' : 'Registrar factura y recibir stock'}</span>
                </button>
                <button className="secondary-button" type="button" onClick={() => setPurchaseForm(createBlankPurchaseForm())}>
                  <Plus size={17} />
                  <span>Nueva factura</span>
                </button>
              </div>
            </form>
          </div>

          <div className="panel purchase-history-panel">
            <div className="panel-heading">
              <div>
                <h2>Facturas recibidas</h2>
                <p>Compras registradas por suplidor.</p>
              </div>
              <button className="secondary-button" onClick={() => loadPurchaseInvoices('')} disabled={loading}>
                <RefreshCw size={17} />
                <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
              </button>
            </div>

            <form
              className="history-search"
              onSubmit={(event) => {
                event.preventDefault();
                loadPurchaseInvoices(purchaseSearch);
              }}
            >
              <label>
                Buscar factura
                <input
                  value={purchaseSearch}
                  onChange={(event) => setPurchaseSearch(event.target.value)}
                  placeholder="Suplidor o numero de factura"
                />
              </label>
              <button className="primary-button" type="submit">
                <Search size={17} />
                <span>Buscar</span>
              </button>
            </form>

            <div className="table-wrap purchase-history-table">
              <table>
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Factura</th>
                    <th>Suplidor</th>
                    <th>Condicion</th>
                    <th>Total</th>
                  </tr>
                </thead>
                <tbody>
                  {purchaseInvoices.map((invoice) => (
                    <tr key={invoice.id}>
                      <td>{new Date(`${invoice.invoiceDate}T00:00:00`).toLocaleDateString('es-DO')}</td>
                      <td>{invoice.documentNumber}</td>
                      <td>
                        <div className="customer-name-cell">
                          <strong>{invoice.supplierName}</strong>
                          <span>
                            {invoice.purchaseOrderNumber
                              ? `${invoice.purchaseOrderNumber} - ${invoice.items.length} productos`
                              : `${invoice.items.length} productos`}
                          </span>
                        </div>
                      </td>
                      <td>
                        <span className={invoice.paymentTerm === 'CREDIT' ? 'status-badge inactive' : 'status-badge active'}>
                          {invoice.paymentTerm === 'CREDIT' ? 'Credito' : 'Contado'}
                        </span>
                      </td>
                      <td>{money.format(Number(invoice.total))}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'movements' && (
        <div className="inventory-two-column">
          <div className="panel inventory-list-panel">
            <div className="panel-heading">
              <div>
                <h2>Movimientos de stock</h2>
                <p>Entradas, salidas y ajustes recientes.</p>
              </div>
              <button className="secondary-button" onClick={() => loadMovements('')} disabled={loading}>
                <RefreshCw size={17} />
                <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
              </button>
            </div>

            <form
              className="movement-filter-form"
              onSubmit={(event) => {
                event.preventDefault();
                loadMovements(movementSearch);
              }}
            >
              <label>
                Buscar movimiento
                <input
                  value={movementSearch}
                  onChange={(event) => setMovementSearch(event.target.value)}
                  placeholder="Producto o referencia"
                />
              </label>
              <label>
                Tipo
                <select
                  value={movementTypeFilter}
                  onChange={(event) => setMovementTypeFilter(event.target.value as InventoryMovementType | '')}
                >
                  <option value="">Todos</option>
                  <option value="SALE">Ventas</option>
                  <option value="PURCHASE">Compras</option>
                  <option value="ADJUSTMENT_IN">Ajustes de entrada</option>
                  <option value="ADJUSTMENT_OUT">Ajustes de salida</option>
                  <option value="RETURN">Devoluciones</option>
                </select>
              </label>
              <label>
                Desde
                <input
                  type="date"
                  value={movementDateFrom}
                  onChange={(event) => setMovementDateFrom(event.target.value)}
                />
              </label>
              <label>
                Hasta
                <input
                  type="date"
                  value={movementDateTo}
                  onChange={(event) => setMovementDateTo(event.target.value)}
                />
              </label>
              <button className="primary-button" type="submit">
                <Search size={17} />
                <span>Filtrar</span>
              </button>
              <button
                className="secondary-button"
                type="button"
                onClick={() => {
                  setMovementSearch('');
                  setMovementTypeFilter('');
                  setMovementDateFrom('');
                  setMovementDateTo('');
                  getInventoryMovements().then(setMovements).catch(() => setMessage('No se pudieron cargar los movimientos.'));
                }}
              >
                <X size={17} />
                <span>Limpiar</span>
              </button>
            </form>

            <div className="table-wrap inventory-table">
              <table>
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Producto</th>
                    <th>Tipo</th>
                    <th>Cantidad</th>
                    {showCosts && <th>Costo</th>}
                    <th>Referencia</th>
                  </tr>
                </thead>
                <tbody>
                  {movements.map((movement) => (
                    <tr key={movement.id}>
                      <td>{formatDateTime(movement.createdAt)}</td>
                      <td>
                        <div className="customer-name-cell">
                          <strong>{movement.productName}</strong>
                          <span>{movement.productSku}</span>
                        </div>
                      </td>
                      <td>
                        <span className={`movement-badge ${movement.movementType.toLowerCase().replace('_', '-')}`}>
                          {movementTypeLabels[movement.movementType]}
                        </span>
                      </td>
                      <td>{formatQuantity(movement.quantity)}</td>
                      {showCosts && <td>{movement.unitCost ? money.format(Number(movement.unitCost)) : 'Sin costo'}</td>}
                      <td>{movement.reference || movement.notes || 'Sin referencia'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="panel inventory-form-panel">
            <div className="panel-heading compact">
              <div>
                <h2>Registrar movimiento</h2>
                <p>Compras, devoluciones y ajustes manuales.</p>
              </div>
              <RefreshCw size={22} />
            </div>

            <form
              className="customer-editor-form"
              onSubmit={(event) => {
                event.preventDefault();
                submitMovement();
              }}
            >
              <label className="span-2">
                Producto
                <select
                  value={movementForm.productId}
                  onChange={(event) => {
                    const product = products.find((item) => item.id === event.target.value);
                    setMovementForm((current) => ({
                      ...current,
                      productId: event.target.value,
                      unitCost: product ? Number(product.costPrice) : current.unitCost
                    }));
                  }}
                >
                  <option value="">Selecciona un producto</option>
                  {activeProducts.map((product) => (
                    <option key={product.id} value={product.id}>
                      {product.sku} - {product.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Tipo
                <select
                  value={movementForm.movementType}
                  onChange={(event) =>
                    setMovementForm((current) => ({ ...current, movementType: event.target.value as InventoryMovementType }))
                  }
                >
                  <option value="PURCHASE">Compra / entrada</option>
                  <option value="ADJUSTMENT_IN">Ajuste entrada</option>
                  <option value="ADJUSTMENT_OUT">Ajuste salida</option>
                  <option value="RETURN">Devolucion</option>
                </select>
              </label>
              <label>
                Cantidad
                <input
                  min={0.01}
                  step={0.01}
                  type="number"
                  value={movementForm.quantity}
                  onChange={(event) => setMovementForm((current) => ({ ...current, quantity: Number(event.target.value) }))}
                />
              </label>
              <label>
                Costo unitario
                <input
                  min={0}
                  step={0.01}
                  type="number"
                  value={movementForm.unitCost}
                  onChange={(event) => setMovementForm((current) => ({ ...current, unitCost: Number(event.target.value) }))}
                />
              </label>
              <label>
                Referencia
                <input
                  value={movementForm.reference}
                  onChange={(event) => setMovementForm((current) => ({ ...current, reference: event.target.value }))}
                  placeholder="Factura compra, ajuste, nota"
                />
              </label>
              <label className="span-2">
                Nota
                <textarea
                  value={movementForm.notes}
                  onChange={(event) => setMovementForm((current) => ({ ...current, notes: event.target.value }))}
                  placeholder="Motivo del movimiento"
                  rows={3}
                />
              </label>
              <div className="customer-form-actions span-2">
                <button className="primary-button" type="submit" disabled={saving}>
                  <Save size={17} />
                  <span>{saving ? 'Registrando...' : 'Registrar movimiento'}</span>
                </button>
                <button className="secondary-button" type="button" onClick={() => setMovementForm(createBlankMovementForm())}>
                  <Plus size={17} />
                  <span>Limpiar</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {activeTab === 'counts' && (
        <div className="count-layout">
          <div className="panel count-entry-panel">
            <div className="panel-heading">
              <div>
                <h2>Nuevo conteo fisico</h2>
                <p>Compara la existencia real con el sistema y registra las diferencias.</p>
              </div>
              <ClipboardCheck size={22} />
            </div>

            <div className="count-summary">
              <div>
                <span>Productos incluidos</span>
                <strong>{countForm.items.filter((item) => item.productId).length}</strong>
              </div>
              <div>
                <span>Con diferencia</span>
                <strong>{countDifferences}</strong>
              </div>
              <div>
                <span>Diferencia neta</span>
                <strong className={countNetDifference === 0 ? '' : countNetDifference > 0 ? 'positive' : 'negative'}>
                  {formatSignedQuantity(countNetDifference)}
                </strong>
              </div>
            </div>

            <div className="count-lines-heading">
              <div>
                <h3>Productos contados</h3>
                <span>La existencia del sistema queda congelada como referencia al registrar.</span>
              </div>
              <button
                className="secondary-button"
                type="button"
                onClick={() =>
                  setCountForm((current) => ({
                    ...current,
                    items: [...current.items, createBlankCountLine()]
                  }))
                }
              >
                <Plus size={17} />
                <span>Agregar producto</span>
              </button>
            </div>

            <div className="count-lines">
              {countForm.items.map((item, index) => {
                const difference = Number(item.countedStock) - Number(item.expectedStock);

                return (
                  <article className="count-line" key={item.key}>
                    <label>
                      Producto
                      <select value={item.productId} onChange={(event) => selectCountProduct(item.key, event.target.value)}>
                        <option value="">Selecciona un producto</option>
                        {activeProducts.map((product) => (
                          <option key={product.id} value={product.id}>
                            {product.sku} - {product.name}
                          </option>
                        ))}
                      </select>
                    </label>
                    <div className="count-readonly">
                      <span>Sistema</span>
                      <strong>{formatQuantity(item.expectedStock)}</strong>
                    </div>
                    <label>
                      Contado
                      <input
                        min={0}
                        step={0.01}
                        type="number"
                        value={item.countedStock}
                        onChange={(event) => updateCountLine(item.key, { countedStock: Number(event.target.value) })}
                      />
                    </label>
                    <div className="count-difference">
                      <span>Diferencia</span>
                      <strong className={difference === 0 ? '' : difference > 0 ? 'positive' : 'negative'}>
                        {formatSignedQuantity(difference)}
                      </strong>
                    </div>
                    <button
                      className="icon-button"
                      type="button"
                      title={`Quitar producto ${index + 1}`}
                      disabled={countForm.items.length === 1}
                      onClick={() =>
                        setCountForm((current) => ({
                          ...current,
                          items: current.items.filter((line) => line.key !== item.key)
                        }))
                      }
                    >
                      <Trash2 size={17} />
                    </button>
                  </article>
                );
              })}
            </div>

            <label className="count-notes">
              Nota del conteo
              <textarea
                value={countForm.notes}
                onChange={(event) => setCountForm((current) => ({ ...current, notes: event.target.value }))}
                placeholder="Area contada, responsable u observaciones"
                rows={3}
              />
            </label>

            <div className="count-actions">
              <button className="primary-button" onClick={submitInventoryCount} disabled={saving}>
                <ClipboardCheck size={17} />
                <span>{saving ? 'Registrando...' : 'Cerrar conteo y ajustar stock'}</span>
              </button>
              <button className="secondary-button" onClick={() => setCountForm(createBlankCountForm())}>
                <Plus size={17} />
                <span>Nuevo conteo</span>
              </button>
            </div>
          </div>

          <div className="panel count-history-panel">
            <div className="panel-heading">
              <div>
                <h2>Conteos anteriores</h2>
                <p>Ultimas revisiones fisicas registradas.</p>
              </div>
              <button className="secondary-button" onClick={loadInventoryCounts} disabled={loading}>
                <RefreshCw size={17} />
                <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
              </button>
            </div>

            <div className="count-history-list">
              {inventoryCounts.map((count) => (
                <article className="count-history-row" key={count.id}>
                  <div>
                    <strong>{count.countNumber}</strong>
                    <span>{formatDateTime(count.countedAt)}</span>
                  </div>
                  <div>
                    <small>Productos</small>
                    <strong>{count.productsCounted}</strong>
                  </div>
                  <div>
                    <small>Diferencias</small>
                    <strong>{count.productsWithDifference}</strong>
                  </div>
                  <div>
                    <small>Balance</small>
                    <strong className={Number(count.netDifference) === 0 ? '' : Number(count.netDifference) > 0 ? 'positive' : 'negative'}>
                      {formatSignedQuantity(count.netDifference)}
                    </strong>
                  </div>
                </article>
              ))}
              {inventoryCounts.length === 0 && (
                <div className="empty-cart">Todavia no hay conteos fisicos registrados.</div>
              )}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'suppliers' && (
        <div className="inventory-two-column">
          <div className="panel inventory-list-panel">
            <div className="panel-heading">
              <div>
                <h2>Suplidores</h2>
                <p>{suppliers.length} suplidores encontrados, {activeSuppliers.length} activos.</p>
              </div>
              <button className="secondary-button" onClick={() => loadSuppliers('')} disabled={loading}>
                <RefreshCw size={17} />
                <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
              </button>
            </div>

            <form
              className="history-search"
              onSubmit={(event) => {
                event.preventDefault();
                loadSuppliers(supplierSearch);
              }}
            >
              <label>
                Buscar suplidor
                <input
                  value={supplierSearch}
                  onChange={(event) => setSupplierSearch(event.target.value)}
                  placeholder="Nombre o RNC"
                />
              </label>
              <button className="primary-button" type="submit">
                <Search size={17} />
                <span>Buscar</span>
              </button>
            </form>

            <div className="table-wrap inventory-table">
              <table>
                <thead>
                  <tr>
                    <th>Suplidor</th>
                    <th>Contacto</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {suppliers.map((supplier) => (
                    <tr key={supplier.id}>
                      <td>
                        <div className="customer-name-cell">
                          <strong>{supplier.name}</strong>
                          <span>{supplier.rnc || 'Sin RNC'}</span>
                        </div>
                      </td>
                      <td>
                        <div className="customer-name-cell">
                          <strong>{supplier.phone || 'Sin telefono'}</strong>
                          <span>{supplier.email || 'Sin correo'}</span>
                        </div>
                      </td>
                      <td>
                        <span className={supplier.active ? 'status-badge active' : 'status-badge inactive'}>
                          {supplier.active ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td>
                        <div className="row-actions">
                          <button className="secondary-button" onClick={() => editSupplier(supplier)}>
                            <Edit3 size={16} />
                            <span>Editar</span>
                          </button>
                          <button className="secondary-button" onClick={() => toggleSupplierStatus(supplier)} disabled={saving}>
                            {supplier.active ? <Ban size={16} /> : <CheckCircle2 size={16} />}
                            <span>{supplier.active ? 'Desactivar' : 'Activar'}</span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="panel inventory-form-panel">
            <div className="panel-heading compact">
              <div>
                <h2>{supplierForm.id ? 'Editar suplidor' : 'Nuevo suplidor'}</h2>
                <p>Datos para compras, cuentas por pagar y reportes.</p>
              </div>
              <Building2 size={22} />
            </div>

            <form
              className="customer-editor-form"
              onSubmit={(event) => {
                event.preventDefault();
                submitSupplier();
              }}
            >
              <label className="span-2">
                Nombre
                <input
                  value={supplierForm.name}
                  onChange={(event) => setSupplierForm((current) => ({ ...current, name: event.target.value }))}
                  placeholder="Nombre o razon social"
                />
              </label>
              <label>
                RNC
                <input
                  value={supplierForm.rnc}
                  onChange={(event) => setSupplierForm((current) => ({ ...current, rnc: event.target.value }))}
                  placeholder="RNC del suplidor"
                />
              </label>
              <button
                className="secondary-button customer-lookup-button"
                type="button"
                onClick={fillSupplierFromTaxpayer}
                disabled={lookingUpSupplier}
              >
                <Search size={17} />
                <span>{lookingUpSupplier ? 'Consultando...' : 'Consultar RNC'}</span>
              </button>
              <label>
                Telefono
                <input
                  value={supplierForm.phone}
                  onChange={(event) => setSupplierForm((current) => ({ ...current, phone: event.target.value }))}
                  placeholder="809-555-0000"
                />
              </label>
              <label>
                Correo
                <input
                  value={supplierForm.email}
                  onChange={(event) => setSupplierForm((current) => ({ ...current, email: event.target.value }))}
                  placeholder="suplidor@empresa.com"
                  type="email"
                />
              </label>
              <label className="span-2">
                Direccion
                <textarea
                  value={supplierForm.address}
                  onChange={(event) => setSupplierForm((current) => ({ ...current, address: event.target.value }))}
                  placeholder="Direccion del suplidor"
                  rows={3}
                />
              </label>
              <label className="customer-active-toggle">
                <input
                  checked={supplierForm.active}
                  onChange={(event) => setSupplierForm((current) => ({ ...current, active: event.target.checked }))}
                  type="checkbox"
                />
                Suplidor activo
              </label>
              <div className="customer-form-actions span-2">
                <button className="primary-button" type="submit" disabled={saving}>
                  <Save size={17} />
                  <span>{saving ? 'Guardando...' : 'Guardar suplidor'}</span>
                </button>
                <button className="secondary-button" type="button" onClick={() => setSupplierForm(createBlankSupplierForm())}>
                  <Plus size={17} />
                  <span>Nuevo</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}

function QuotesView({
  apiOnline,
  company,
  products,
  onQuoteInvoiced
}: {
  apiOnline: boolean;
  company: CompanyProfile;
  products: Product[];
  onQuoteInvoiced: () => void;
}) {
  const [quoteList, setQuoteList] = useState<Quote[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [form, setForm] = useState<QuoteFormState>(createBlankQuoteForm());
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  const subtotal = form.items.reduce(
    (sum, item) => sum + Number(item.quantity) * Number(item.unitPrice),
    0
  );
  const taxTotal = form.items.reduce(
    (sum, item) => sum + Number(item.quantity) * Number(item.unitPrice) * (Number(item.taxRate) / 100),
    0
  );
  const total = subtotal + taxTotal;
  const activeQuotes = quoteList.filter((quote) => !['CANCELLED', 'EXPIRED', 'CONVERTED'].includes(quote.status));
  const approvedQuotes = quoteList.filter((quote) => quote.status === 'APPROVED').length;

  const loadQuotes = async (term = search) => {
    if (!apiOnline) {
      setQuoteList([]);
      setMessage('El backend debe estar conectado para gestionar cotizaciones.');
      return;
    }

    setLoading(true);
    setMessage('');
    try {
      const data = await getQuotes(term);
      setQuoteList(data);
      if (data.length === 0) {
        setMessage('No hay cotizaciones para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar las cotizaciones.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!apiOnline) {
      setCustomers([]);
      setQuoteList([]);
      return;
    }

    void Promise.all([
      getCustomers().then((data) => setCustomers(data.filter((customer) => customer.active))),
      getQuotes().then(setQuoteList)
    ]).catch(() => setMessage('No se pudieron cargar los datos de cotizaciones.'));
  }, [apiOnline]);

  const selectCustomer = (customerId: string) => {
    const customer = customers.find((item) => item.id === customerId);
    setForm((current) => ({
      ...current,
      customerId,
      customerName: customer?.name ?? '',
      customerFiscalId: customer?.fiscalId ?? ''
    }));
  };

  const updateLine = (key: string, changes: Partial<QuoteLineFormState>) => {
    setForm((current) => ({
      ...current,
      items: current.items.map((item) => (item.key === key ? { ...item, ...changes } : item))
    }));
  };

  const selectProduct = (key: string, productId: string) => {
    const product = products.find((item) => item.id === productId);
    updateLine(key, {
      productId,
      productName: product?.name ?? '',
      unitPrice: Number(product?.salePrice ?? 0),
      taxRate: Number(product?.taxRate ?? 18)
    });
  };

  const resetForm = () => {
    setForm(createBlankQuoteForm());
    setMessage('');
  };

  const editQuote = (quote: Quote) => {
    const matchingCustomer = customers.find(
      (customer) => customer.name === quote.customerName
        || (quote.customerFiscalId && customer.fiscalId === quote.customerFiscalId)
    );
    setForm({
      id: quote.id,
      customerId: matchingCustomer?.id ?? '',
      customerName: quote.customerName,
      customerFiscalId: quote.customerFiscalId ?? '',
      issueDate: quote.issueDate,
      validUntil: quote.validUntil,
      status: quote.status,
      notes: quote.notes ?? '',
      items: quote.items.map((item) => {
        const product = products.find((candidate) => candidate.id === item.productId || candidate.name === item.productName);
        return {
          key: item.id,
          productId: item.productId ?? product?.id ?? '',
          productName: item.productName,
          quantity: Number(item.quantity),
          unitPrice: Number(item.unitPrice),
          taxRate: Number(item.taxRate)
        };
      })
    });
    setMessage(`Editando ${quote.quoteNumber}.`);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const submitQuote = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar cotizaciones.');
      return;
    }
    if (!form.customerName.trim()) {
      setMessage('Selecciona o escribe el cliente de la cotizacion.');
      return;
    }
    if (form.items.some((item) => !item.productName.trim() || Number(item.quantity) <= 0)) {
      setMessage('Cada linea necesita un producto y una cantidad mayor que cero.');
      return;
    }
    if (form.validUntil < form.issueDate) {
      setMessage('La fecha de validez no puede ser anterior a la fecha de emision.');
      return;
    }

    setSaving(true);
    setMessage('');
    try {
      const request = quoteFormToRequest(form);
      const saved = form.id
        ? await updateQuote(form.id, request)
        : await createQuote(request);
      setForm(createBlankQuoteForm());
      await loadQuotes('');
      setMessage(`${saved.quoteNumber} guardada por ${money.format(Number(saved.total))}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar la cotizacion.');
    } finally {
      setSaving(false);
    }
  };

  const changeQuoteStatus = async (quote: Quote, status: QuoteStatus) => {
    setSaving(true);
    setMessage('');
    try {
      const updated = await updateQuote(quote.id, quoteToRequest(quote, status));
      setQuoteList((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      setMessage(`${updated.quoteNumber} ahora esta ${quoteStatusLabels[updated.status].toLowerCase()}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo cambiar el estado de la cotizacion.');
    } finally {
      setSaving(false);
    }
  };

  const invoiceApprovedQuote = async (quote: Quote) => {
    setSaving(true);
    setMessage('');
    try {
      const sale = await invoiceQuote(quote.id);
      await loadQuotes('');
      onQuoteInvoiced();
      printSaleInvoice(sale, company);
      setMessage(`${quote.quoteNumber} fue facturada como ${sale.invoiceNumber} con NCF ${sale.ncf ?? 'pendiente'}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo facturar la cotizacion.');
    } finally {
      setSaving(false);
    }
  };

  const exportQuote = async (quote: Quote, format: ReportFormat) => {
    setLoading(true);
    setMessage('');
    try {
      const blob = await downloadQuoteReport(quote.id, format);
      downloadBlob(blob, `${quote.quoteNumber}.${format}`);
      setMessage(`${quote.quoteNumber} descargada en ${format.toUpperCase()}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo generar la cotizacion.');
    } finally {
      setLoading(false);
    }
  };

  const printQuote = async (quote: Quote) => {
    const printWindow = window.open('', '_blank');
    if (printWindow) {
      printWindow.opener = null;
      printWindow.document.title = `Preparando ${quote.quoteNumber}`;
      printWindow.document.body.innerHTML = '<p style="font-family: sans-serif; padding: 24px;">Preparando cotizacion para imprimir...</p>';
    }

    setLoading(true);
    setMessage('');
    try {
      const blob = await downloadQuoteReport(quote.id, 'pdf', true);
      const url = URL.createObjectURL(blob);
      if (!printWindow) {
        downloadBlob(blob, `${quote.quoteNumber}.pdf`);
        setMessage('El navegador bloqueo la ventana; se descargo el PDF para imprimir.');
      } else {
        printWindow.location.href = url;
        setMessage(`${quote.quoteNumber} abierta para imprimir.`);
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      }
    } catch (error) {
      printWindow?.close();
      setMessage(error instanceof Error ? error.message : 'No se pudo abrir la cotizacion.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="quotes-page">
      <div className="inventory-kpi-grid">
        <MetricCard label="Cotizaciones" value={quoteList.length.toString()} tone="green" />
        <MetricCard label="Activas" value={activeQuotes.length.toString()} tone="blue" />
        <MetricCard label="Aprobadas" value={approvedQuotes.toString()} tone="sand" />
        <MetricCard label="Valor activo" value={money.format(activeQuotes.reduce((sum, quote) => sum + Number(quote.total), 0))} tone="red" />
      </div>

      {message && <div className="form-message">{message}</div>}

      <div className="quotes-layout">
        <div className="panel quote-editor-panel">
          <div className="panel-heading">
            <div>
              <h2>{form.id ? 'Editar cotizacion' : 'Nueva cotizacion'}</h2>
              <p>Prepara precios, vigencia y condiciones antes de enviarla al cliente.</p>
            </div>
            <FileText size={22} />
          </div>

          <div className="quote-header-form">
            <label>
              Cliente registrado
              <select value={form.customerId} onChange={(event) => selectCustomer(event.target.value)}>
                <option value="">Cliente no registrado</option>
                {customers.map((customer) => (
                  <option key={customer.id} value={customer.id}>
                    {customer.name}{customer.fiscalId ? ` - ${customer.fiscalId}` : ''}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Nombre del cliente
              <input
                value={form.customerName}
                onChange={(event) => setForm((current) => ({ ...current, customerName: event.target.value }))}
                placeholder="Persona o empresa"
              />
            </label>
            <label>
              RNC o cedula
              <input
                value={form.customerFiscalId}
                onChange={(event) => setForm((current) => ({ ...current, customerFiscalId: event.target.value }))}
                placeholder="Opcional"
              />
            </label>
            <label>
              Fecha de emision
              <input
                type="date"
                value={form.issueDate}
                onChange={(event) => setForm((current) => ({ ...current, issueDate: event.target.value }))}
              />
            </label>
            <label>
              Valida hasta
              <input
                type="date"
                value={form.validUntil}
                onChange={(event) => setForm((current) => ({ ...current, validUntil: event.target.value }))}
              />
            </label>
            <label>
              Estado
              <select
                value={form.status}
                onChange={(event) => setForm((current) => ({ ...current, status: event.target.value as QuoteStatus }))}
              >
                {Object.entries(quoteStatusLabels).map(([value, label]) => (
                  <option key={value} value={value}>{label}</option>
                ))}
              </select>
            </label>
          </div>

          <div className="purchase-lines-heading">
            <div>
              <h3>Productos cotizados</h3>
              <span>Los precios se toman del catalogo y pueden ajustarse antes de guardar.</span>
            </div>
            <button
              className="secondary-button"
              type="button"
              onClick={() => setForm((current) => ({ ...current, items: [...current.items, createBlankQuoteLine()] }))}
            >
              <Plus size={17} />
              <span>Agregar linea</span>
            </button>
          </div>

          <div className="quote-lines">
            {form.items.map((item, index) => {
              const lineSubtotal = Number(item.quantity) * Number(item.unitPrice);
              const lineTotal = lineSubtotal + lineSubtotal * (Number(item.taxRate) / 100);
              return (
                <article className="quote-line" key={item.key}>
                  <label className="quote-product-field">
                    Producto
                    <select value={item.productId} onChange={(event) => selectProduct(item.key, event.target.value)}>
                      <option value="">Selecciona del catalogo</option>
                      {products.map((product) => (
                        <option key={product.id} value={product.id}>{product.sku} - {product.name}</option>
                      ))}
                    </select>
                    {!item.productId && (
                      <input
                        value={item.productName}
                        onChange={(event) => updateLine(item.key, { productName: event.target.value })}
                        placeholder="O escribe un producto o servicio"
                      />
                    )}
                  </label>
                  <label>
                    Cantidad
                    <input
                      min={0.01}
                      step={0.01}
                      type="number"
                      value={item.quantity}
                      onChange={(event) => updateLine(item.key, { quantity: Number(event.target.value) })}
                    />
                  </label>
                  <label>
                    Precio
                    <input
                      min={0}
                      step={0.01}
                      type="number"
                      value={item.unitPrice}
                      onChange={(event) => updateLine(item.key, { unitPrice: Number(event.target.value) })}
                    />
                  </label>
                  <label>
                    ITBIS %
                    <input
                      min={0}
                      step={0.01}
                      type="number"
                      value={item.taxRate}
                      onChange={(event) => updateLine(item.key, { taxRate: Number(event.target.value) })}
                    />
                  </label>
                  <div className="quote-line-total">
                    <span>Total</span>
                    <strong>{money.format(lineTotal)}</strong>
                  </div>
                  <button
                    className="icon-button"
                    type="button"
                    title={`Quitar linea ${index + 1}`}
                    disabled={form.items.length === 1}
                    onClick={() => setForm((current) => ({
                      ...current,
                      items: current.items.filter((line) => line.key !== item.key)
                    }))}
                  >
                    <Trash2 size={17} />
                  </button>
                </article>
              );
            })}
          </div>

          <div className="quote-footer">
            <label>
              Condiciones y notas
              <textarea
                value={form.notes}
                onChange={(event) => setForm((current) => ({ ...current, notes: event.target.value }))}
                rows={4}
              />
            </label>
            <div className="purchase-totals">
              <div><span>Subtotal</span><strong>{money.format(subtotal)}</strong></div>
              <div><span>ITBIS</span><strong>{money.format(taxTotal)}</strong></div>
              <div className="purchase-total"><span>Total</span><strong>{money.format(total)}</strong></div>
            </div>
          </div>

          <div className="purchase-actions">
            <button className="primary-button" onClick={submitQuote} disabled={saving}>
              <Save size={17} />
              <span>{saving ? 'Guardando...' : form.id ? 'Actualizar cotizacion' : 'Crear cotizacion'}</span>
            </button>
            <button className="secondary-button" onClick={resetForm}>
              <Plus size={17} />
              <span>Nueva</span>
            </button>
          </div>
        </div>

        <div className="panel quote-history-panel">
          <div className="panel-heading">
            <div>
              <h2>Cotizaciones emitidas</h2>
              <p>Borradores, enviadas, aprobadas y documentos finales.</p>
            </div>
            <button className="secondary-button" onClick={() => loadQuotes('')} disabled={loading}>
              <RefreshCw size={17} />
              <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
            </button>
          </div>

          <form
            className="history-search"
            onSubmit={(event) => {
              event.preventDefault();
              loadQuotes(search);
            }}
          >
            <label>
              Buscar cotizacion
              <input
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                placeholder="Numero o cliente"
              />
            </label>
            <button className="primary-button" type="submit">
              <Search size={17} />
              <span>Buscar</span>
            </button>
          </form>

          <div className="quote-history-list">
            {quoteList.map((quote) => (
              <article className="quote-history-card" key={quote.id}>
                <div className="quote-history-heading">
                  <div>
                    <strong>{quote.quoteNumber}</strong>
                    <span>{quote.customerName}</span>
                  </div>
                  <span className={`quote-status ${quote.status.toLowerCase()}`}>
                    {quoteStatusLabels[quote.status]}
                  </span>
                </div>
                <div className="quote-history-meta">
                  <span>Valida hasta {new Date(`${quote.validUntil}T00:00:00`).toLocaleDateString('es-DO')}</span>
                  <span>{quote.items.length} productos</span>
                  <strong>{money.format(Number(quote.total))}</strong>
                </div>
                <div className="quote-history-actions">
                  <button className="secondary-button" onClick={() => editQuote(quote)}>
                    <Edit3 size={16} />
                    <span>Editar</span>
                  </button>
                  {quote.status === 'DRAFT' && (
                    <button className="secondary-button" onClick={() => changeQuoteStatus(quote, 'SENT')} disabled={saving}>
                      <CheckCircle2 size={16} />
                      <span>Marcar enviada</span>
                    </button>
                  )}
                  {quote.status === 'SENT' && (
                    <button className="primary-button" onClick={() => changeQuoteStatus(quote, 'APPROVED')} disabled={saving}>
                      <CheckCircle2 size={16} />
                      <span>Aprobar</span>
                    </button>
                  )}
                  {quote.status === 'APPROVED' && (
                    <button className="primary-button" onClick={() => invoiceApprovedQuote(quote)} disabled={saving}>
                      <ReceiptText size={16} />
                      <span>Facturar</span>
                    </button>
                  )}
                  {!['CANCELLED', 'CONVERTED'].includes(quote.status) && (
                    <button className="secondary-button" onClick={() => changeQuoteStatus(quote, 'CANCELLED')} disabled={saving}>
                      <Ban size={16} />
                      <span>Cancelar</span>
                    </button>
                  )}
                </div>
                <div className="purchase-order-documents">
                  <button className="secondary-button" onClick={() => printQuote(quote)} disabled={loading}>
                    <Printer size={16} />
                    <span>Imprimir</span>
                  </button>
                  <button className="secondary-button" onClick={() => exportQuote(quote, 'pdf')} disabled={loading}>
                    <Download size={16} />
                    <span>PDF</span>
                  </button>
                  <button className="secondary-button" onClick={() => exportQuote(quote, 'xlsx')} disabled={loading}>
                    <FileSpreadsheet size={16} />
                    <span>Excel</span>
                  </button>
                  <button className="secondary-button" onClick={() => exportQuote(quote, 'docx')} disabled={loading}>
                    <FileText size={16} />
                    <span>Word</span>
                  </button>
                </div>
              </article>
            ))}
            {quoteList.length === 0 && <div className="empty-cart">Todavia no hay cotizaciones registradas.</div>}
          </div>
        </div>
      </div>
    </section>
  );
}

function SalesHistoryView({ apiOnline, company }: { apiOnline: boolean; company: CompanyProfile }) {
  const [sales, setSales] = useState<SaleResponse[]>([]);
  const [selectedSale, setSelectedSale] = useState<SaleResponse | null>(null);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const loadSales = async (term = search) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para ver el historial.');
      setSales([]);
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      const data = await getSales(term);
      setSales(data);
      if (data.length === 0) {
        setMessage('No hay ventas para mostrar.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudieron cargar las ventas.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSales('');
    // Solo se carga al entrar al modulo; las busquedas se controlan con el formulario.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiOnline]);

  return (
    <section className="panel sales-history-panel">
      <div className="panel-heading">
        <div>
          <h2>Ventas emitidas</h2>
          <p>Consulta facturas recientes y reimprime cualquier comprobante.</p>
        </div>
        <button className="secondary-button" onClick={() => loadSales('')} disabled={loading}>
          <ReceiptText size={17} />
          <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
        </button>
      </div>

      <form
        className="history-search"
        onSubmit={(event) => {
          event.preventDefault();
          loadSales(search);
        }}
      >
        <label>
          Buscar venta
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Factura o cliente"
          />
        </label>
        <button className="primary-button" type="submit">
          <Search size={17} />
          <span>Buscar</span>
        </button>
      </form>

      {message && <div className="form-message">{message}</div>}

      <div className="sales-list">
        {sales.map((sale) => (
          <article className="sale-card" key={sale.id}>
            <div>
              <strong>{sale.invoiceNumber}</strong>
              <span>{sale.customerName}</span>
            </div>
            <div>
              <small>Fecha</small>
              <span>{formatDateTime(sale.issuedAt)}</span>
            </div>
            <div>
              <small>Total</small>
              <span>{money.format(Number(sale.total))}</span>
            </div>
            <div className="sale-actions">
              <button className="secondary-button" onClick={() => setSelectedSale(sale)}>
                <Eye size={17} />
                <span>Ver factura</span>
              </button>
              <button className="secondary-button" onClick={() => printSaleInvoice(sale, company)}>
                <Printer size={17} />
                <span>Reimprimir</span>
              </button>
            </div>
          </article>
        ))}
      </div>

      {selectedSale && (
        <InvoicePreviewModal
          sale={selectedSale}
          company={company}
          onClose={() => setSelectedSale(null)}
        />
      )}
    </section>
  );
}

function InvoicePreviewModal({
  sale,
  company,
  onClose
}: {
  sale: SaleResponse;
  company: CompanyProfile;
  onClose: () => void;
}) {
  useEffect(() => {
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    window.addEventListener('keydown', closeOnEscape);
    return () => window.removeEventListener('keydown', closeOnEscape);
  }, [onClose]);

  return (
    <div className="invoice-modal-backdrop" role="presentation">
      <section className="invoice-modal" role="dialog" aria-modal="true" aria-labelledby="invoice-preview-title">
        <header className="invoice-modal-toolbar">
          <div>
            <span>Factura emitida</span>
            <h2 id="invoice-preview-title">{sale.invoiceNumber}</h2>
          </div>
          <div className="invoice-toolbar-actions">
            <button className="secondary-button" onClick={() => printSaleInvoice(sale, company)}>
              <Printer size={17} />
              <span>Imprimir</span>
            </button>
            <button className="icon-button" onClick={onClose} title="Cerrar">
              <X size={18} />
            </button>
          </div>
        </header>

        <InvoiceDocument sale={sale} company={company} />
      </section>
    </div>
  );
}

function InvoiceDocument({ sale, company }: { sale: SaleResponse; company: CompanyProfile }) {
  const businessName = company.commercialName || company.name;

  return (
    <article className="invoice-document">
      <section className="invoice-document-header">
        <div>
          <h3>{businessName}</h3>
          <p>{company.rnc ? `RNC ${company.rnc}` : company.name}</p>
          <p>{company.address || 'Direccion no configurada'}</p>
          <p>{company.phone || 'Telefono no configurado'}</p>
        </div>
        <div>
          <strong>Factura de venta</strong>
          <span>No. {sale.invoiceNumber}</span>
          <span>Fecha: {formatDateTime(sale.issuedAt)}</span>
          <span>NCF: {sale.ncf || 'Pendiente'}</span>
          <span>e-CF: {ecfStatusLabels[sale.ecfStatus ?? 'NOT_SUBMITTED']}</span>
        </div>
      </section>

      <section className="invoice-customer-block">
        <h4>Cliente</h4>
        <p>{sale.customerName}</p>
        <span>{sale.customerFiscalId ? `RNC/Cedula: ${sale.customerFiscalId}` : 'Cliente de contado'}</span>
      </section>

      <section className="invoice-lines-block">
        <div className="invoice-lines-heading">
          <span>Producto</span>
          <span>Cant.</span>
          <span>Precio</span>
          <span>Total</span>
        </div>
        {sale.items.map((item) => (
          <div className="invoice-line" key={item.id}>
            <span>{item.productName}</span>
            <span>{formatQuantity(item.quantity)}</span>
            <span>{money.format(Number(item.unitPrice))}</span>
            <span>{money.format(Number(item.lineTotal))}</span>
          </div>
        ))}
      </section>

      <section className="invoice-summary">
        <div>
          <span>Subtotal</span>
          <strong>{money.format(Number(sale.subtotal))}</strong>
        </div>
        <div>
          <span>ITBIS</span>
          <strong>{money.format(Number(sale.taxTotal))}</strong>
        </div>
        <div>
          <span>Descuento</span>
          <strong>{money.format(Number(sale.discountTotal))}</strong>
        </div>
        <div className="invoice-summary-total">
          <span>Total</span>
          <strong>{money.format(Number(sale.total))}</strong>
        </div>
      </section>

      <section className="invoice-payments">
        <h4>Pagos</h4>
        {sale.payments.length === 0 && <span>Sin pago registrado</span>}
        {sale.payments.map((payment) => (
          <span key={payment.id}>
            {paymentLabels[payment.method]}
            {payment.processor ? ` (${paymentProcessorLabels[payment.processor]})` : ''}: {money.format(Number(payment.amount))}
            {payment.reference ? ` - ${payment.reference}` : ''}
          </span>
        ))}
      </section>
    </article>
  );
}

function PosView({
  apiOnline,
  company,
  products,
  onSaleCreated
}: {
  apiOnline: boolean;
  company: CompanyProfile;
  products: Product[];
  onSaleCreated: () => void;
}) {
  const [cart, setCart] = useState<CartItem[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [customerId, setCustomerId] = useState('');
  const [customerName, setCustomerName] = useState('Cliente de contado');
  const [customerFiscalId, setCustomerFiscalId] = useState('');
  const [customerSearch, setCustomerSearch] = useState('');
  const [customerLoadMessage, setCustomerLoadMessage] = useState('');
  const [lookingUpSaleTaxpayer, setLookingUpSaleTaxpayer] = useState(false);
  const [productSearch, setProductSearch] = useState('');
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('CASH');
  const [cardProcessor, setCardProcessor] = useState<PaymentProcessor>('AZUL');
  const [paymentReference, setPaymentReference] = useState('');
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');
  const [lastSale, setLastSale] = useState<SaleResponse | null>(null);

  const activeCustomers = useMemo(() => customers.filter((customer) => customer.active), [customers]);
  const filteredCustomers = useMemo(() => {
    const searchTerm = normalizeSearchText(customerSearch);

    if (!searchTerm) {
      return activeCustomers;
    }

    return activeCustomers.filter((customer) => customerMatchesSearch(customer, searchTerm));
  }, [activeCustomers, customerSearch]);
  const selectedCustomer = useMemo(
    () => activeCustomers.find((customer) => customer.id === customerId),
    [activeCustomers, customerId]
  );
  const customerOptions = useMemo(() => {
    if (selectedCustomer && !filteredCustomers.some((customer) => customer.id === selectedCustomer.id)) {
      return [selectedCustomer, ...filteredCustomers];
    }

    return filteredCustomers;
  }, [filteredCustomers, selectedCustomer]);
  const availableProducts = useMemo(() => {
    const searchTerm = normalizeSearchText(productSearch);
    return products.filter(
      (product) => product.active && product.currentStock > 0 && productMatchesSearch(product, searchTerm)
    );
  }, [products, productSearch]);
  const subtotal = cart.reduce((sum, item) => sum + item.product.salePrice * item.quantity, 0);
  const taxTotal = cart.reduce(
    (sum, item) => sum + item.product.salePrice * item.quantity * (taxRateForCustomer(item.product, selectedCustomer) / 100),
    0
  );
  const discountTotal = 0;
  const total = Math.max(subtotal + taxTotal - discountTotal, 0);
  const selectedCustomerCreditLimit = Number(selectedCustomer?.creditLimit ?? 0);
  const canUseCredit = Boolean(selectedCustomer && selectedCustomerCreditLimit > 0);
  const saleHasTaxExemption = isTaxExemptCustomer(selectedCustomer);

  useEffect(() => {
    if (!apiOnline) {
      setCustomers([]);
      setCustomerId('');
      setCustomerSearch('');
      setCustomerLoadMessage('');
      return;
    }

    let cancelled = false;
    setCustomerLoadMessage('');

    getCustomers()
      .then((data) => {
        if (!cancelled) {
          setCustomers(data.filter((customer) => customer.active));
        }
      })
      .catch(() => {
        if (!cancelled) {
          setCustomers([]);
          setCustomerLoadMessage('No se pudieron cargar los clientes registrados.');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [apiOnline]);

  useEffect(() => {
    if (paymentMethod === 'CREDIT' && !canUseCredit) {
      setPaymentMethod('CASH');
    }
  }, [canUseCredit, paymentMethod]);

  const selectCustomer = (selectedCustomerId: string) => {
    setCustomerId(selectedCustomerId);
    setLastSale(null);
    setMessage('');

    if (!selectedCustomerId) {
      setCustomerName('Cliente de contado');
      setCustomerFiscalId('');
      setCustomerSearch('');
      if (paymentMethod === 'CREDIT') {
        setPaymentMethod('CASH');
      }
      return;
    }

    const selectedCustomer = activeCustomers.find((customer) => customer.id === selectedCustomerId);

    if (selectedCustomer) {
      setCustomerName(selectedCustomer.name);
      setCustomerFiscalId(selectedCustomer.fiscalId ?? '');
      setCustomerSearch(selectedCustomer.name);
      if (Number(selectedCustomer.creditLimit) <= 0 && paymentMethod === 'CREDIT') {
        setPaymentMethod('CASH');
      }
    }
  };

  const fillSaleCustomerFromTaxpayer = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para consultar RNC.');
      return;
    }

    if (!customerFiscalId.trim()) {
      setMessage('Escribe el RNC o cedula antes de consultar.');
      return;
    }

    setLookingUpSaleTaxpayer(true);
    setMessage('');

    try {
      const taxpayer = await lookupTaxpayerByRnc(customerFiscalId);
      setCustomerId('');
      setCustomerSearch('');
      setCustomerName(taxpayer.name);
      setCustomerFiscalId(taxpayer.rnc || customerFiscalId);
      setMessage(taxpayer.fiscalProfile && taxpayer.fiscalProfile !== 'STANDARD' && taxpayer.fiscalProfile !== 'TAX_CREDIT'
        ? `RNC encontrado en ${taxpayer.source}: ${taxpayer.name}. Para aplicar ${customerFiscalProfileLabels[taxpayer.fiscalProfile]}, registra o selecciona este cliente.`
        : `RNC encontrado en ${taxpayer.source}: ${taxpayer.name}.`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo consultar el RNC.');
    } finally {
      setLookingUpSaleTaxpayer(false);
    }
  };

  const addProduct = (product: Product) => {
    setMessage('');
    setLastSale(null);
    setCart((current) => {
      const existing = current.find((item) => item.product.id === product.id);

      if (existing) {
        return current.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: Math.min(item.quantity + 1, item.product.currentStock) }
            : item
        );
      }

      return [...current, { product, quantity: 1 }];
    });
  };

  const changeQuantity = (productId: string, quantity: number) => {
    setCart((current) =>
      current
        .map((item) =>
          item.product.id === productId
            ? { ...item, quantity: Math.max(1, Math.min(quantity, item.product.currentStock)) }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  const removeProduct = (productId: string) => {
    setCart((current) => current.filter((item) => item.product.id !== productId));
  };

  const submitSale = async () => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para registrar ventas.');
      return;
    }

    if (cart.length === 0) {
      setMessage('Agrega al menos un producto al carrito.');
      return;
    }

    if (paymentMethod === 'CARD' && !paymentReference.trim()) {
      setMessage('El pago con tarjeta necesita el comprobante.');
      return;
    }

    if (paymentMethod === 'CREDIT') {
      if (!selectedCustomer) {
        setMessage('El credito solo esta disponible para clientes registrados.');
        return;
      }

      if (selectedCustomerCreditLimit <= 0) {
        setMessage('Este cliente no tiene credito aprobado.');
        return;
      }

      if (total > selectedCustomerCreditLimit) {
        setMessage(`El total excede el credito aprobado de ${money.format(selectedCustomerCreditLimit)}.`);
        return;
      }
    }

    setBusy(true);
    setMessage('');
    setLastSale(null);

    try {
      const sale: SaleResponse = await createSale({
        customerId: customerId || undefined,
        customerName,
        customerFiscalId: customerFiscalId || undefined,
        discountTotal,
        items: cart.map((item) => ({
          productId: item.product.id,
          quantity: item.quantity
        })),
        payments: [
          {
            method: paymentMethod,
            amount: total,
            processor: paymentMethod === 'CARD' ? cardProcessor : undefined,
            reference: optionalText(paymentReference)
          }
        ]
      });

      setCart([]);
      setCustomerId('');
      setCustomerName('Cliente de contado');
      setCustomerFiscalId('');
      setCustomerSearch('');
      setCardProcessor('AZUL');
      setPaymentReference('');
      setLastSale(sale);
      setMessage(`Venta registrada: ${sale.invoiceNumber} por ${money.format(sale.total)}.`);
      onSaleCreated();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo registrar la venta.');
    } finally {
      setBusy(false);
    }
  };

  return (
    <section className="pos-grid">
      <div className="panel pos-products">
        <div className="panel-heading">
          <div>
            <h2>Productos</h2>
            <p>Selecciona articulos activos y con stock.</p>
          </div>
        </div>

        <label className="product-search">
          Buscar producto
          <div>
            <Search size={17} />
            <input
              value={productSearch}
              onChange={(event) => setProductSearch(event.target.value)}
              placeholder="Nombre, codigo, categoria o marca"
            />
          </div>
        </label>

        <div className="product-picker">
          {availableProducts.map((product) => (
            <button
              className="product-tile"
              key={product.id}
              onClick={() => addProduct(product)}
            >
              <strong>{product.name}</strong>
              <span>
                {product.sku}
                {product.categoryName ? ` - ${product.categoryName}` : ''}
              </span>
              <small>
                {money.format(product.salePrice)} - Stock {product.currentStock}
              </small>
            </button>
          ))}
          {availableProducts.length === 0 && (
            <div className="empty-products">No hay productos disponibles para esa busqueda.</div>
          )}
        </div>
      </div>

      <div className="panel pos-cart">
        <div className="panel-heading">
          <div>
            <h2>Venta actual</h2>
            <p>Totales con ITBIS.</p>
          </div>
          <ReceiptText size={22} />
        </div>

        <div className="customer-form">
          <div className="customer-lookup span-2">
            <label>
              Cliente registrado
              <div className="lookup-row">
                <select value={customerId} onChange={(event) => selectCustomer(event.target.value)}>
                  <option value="">Cliente de contado</option>
                  {customerOptions.map((customer) => (
                    <option key={customer.id} value={customer.id}>
                      {customer.name}
                      {customer.fiscalId ? ` - ${customer.fiscalId}` : ''}
                    </option>
                  ))}
                </select>
                <div className="lookup-search">
                  <Search size={17} />
                  <input
                    value={customerSearch}
                    onChange={(event) => setCustomerSearch(event.target.value)}
                    placeholder="Buscar cliente"
                  />
                </div>
              </div>
            </label>
          </div>
          {customerSearch.trim() && customerOptions.length === 0 && (
            <div className="field-note span-2">No hay clientes registrados con esa busqueda.</div>
          )}
          {selectedCustomer && (
            <div className="credit-note span-2">
              <span>{selectedCustomerCreditLimit > 0 ? 'Credito aprobado' : 'Sin credito aprobado'}</span>
              <strong>{money.format(selectedCustomerCreditLimit)}</strong>
            </div>
          )}
          {saleHasTaxExemption && (
            <div className="field-note span-2">
              Este cliente esta marcado como {customerFiscalProfileLabels[selectedCustomer?.fiscalProfile ?? 'STANDARD']}; el ITBIS se calcula en 0.
            </div>
          )}
          {customerLoadMessage && <div className="field-note span-2">{customerLoadMessage}</div>}
          <label>
            Cliente
            <input value={customerName} onChange={(event) => setCustomerName(event.target.value)} />
          </label>
          <label>
            RNC o cedula
            <input value={customerFiscalId} onChange={(event) => setCustomerFiscalId(event.target.value)} />
          </label>
          <button
            className="secondary-button customer-lookup-button span-2"
            onClick={fillSaleCustomerFromTaxpayer}
            disabled={lookingUpSaleTaxpayer}
          >
            <Search size={17} />
            <span>{lookingUpSaleTaxpayer ? 'Consultando...' : 'Consultar RNC'}</span>
          </button>
        </div>

        <div className="cart-lines">
          {cart.length === 0 && <div className="empty-cart">El carrito esta vacio.</div>}
          {cart.map((item) => (
            <article className="cart-line" key={item.product.id}>
              <div>
                <strong>{item.product.name}</strong>
                <span>{money.format(item.product.salePrice)} x {item.quantity}</span>
              </div>
              <div className="quantity-control">
                <button title="Restar" onClick={() => changeQuantity(item.product.id, item.quantity - 1)}>
                  <Minus size={15} />
                </button>
                <input
                  aria-label={`Cantidad de ${item.product.name}`}
                  min={1}
                  max={item.product.currentStock}
                  type="number"
                  value={item.quantity}
                  onChange={(event) => changeQuantity(item.product.id, Number(event.target.value))}
                />
                <button title="Sumar" onClick={() => changeQuantity(item.product.id, item.quantity + 1)}>
                  <Plus size={15} />
                </button>
                <button title="Quitar" onClick={() => removeProduct(item.product.id)}>
                  <Trash2 size={15} />
                </button>
              </div>
            </article>
          ))}
        </div>

        <div className="payment-form">
          <label>
            Metodo de pago
            <select
              value={paymentMethod}
              onChange={(event) => {
                const method = event.target.value as PaymentMethod;
                setPaymentMethod(method);
                if (method !== 'CARD') {
                  setPaymentReference('');
                }
              }}
            >
              <option value="CASH">Efectivo</option>
              <option value="CARD">Tarjeta</option>
              <option value="CREDIT" disabled={!canUseCredit}>Credito</option>
            </select>
          </label>
          {paymentMethod === 'CARD' && (
            <>
              <label>
                Procesador
                <select value={cardProcessor} onChange={(event) => setCardProcessor(event.target.value as PaymentProcessor)}>
                  <option value="AZUL">Azul</option>
                  <option value="CARDNET">CardNet</option>
                </select>
              </label>
              <label>
                Comprobante tarjeta
                <input
                  value={paymentReference}
                  onChange={(event) => setPaymentReference(event.target.value)}
                  placeholder="Autorizacion o voucher"
                />
              </label>
            </>
          )}
        </div>

        <div className="totals-box">
          <div>
            <span>Subtotal</span>
            <strong>{money.format(subtotal)}</strong>
          </div>
          <div>
            <span>ITBIS</span>
            <strong>{money.format(taxTotal)}</strong>
          </div>
          <div className="total-row">
            <span>Total</span>
            <strong>{money.format(total)}</strong>
          </div>
        </div>

        {message && <div className="form-message">{message}</div>}

        {lastSale && (
          <div className="post-sale-actions">
            <button className="secondary-button" onClick={() => printSaleInvoice(lastSale, company)}>
              <Printer size={17} />
              <span>Imprimir factura</span>
            </button>
            <button className="secondary-button" onClick={() => setLastSale(null)}>
              <Plus size={17} />
              <span>Nueva venta</span>
            </button>
          </div>
        )}

        <button className="primary-button checkout-button" onClick={submitSale} disabled={busy || cart.length === 0}>
          <ReceiptText size={18} />
          <span>{busy ? 'Registrando...' : 'Registrar venta'}</span>
        </button>
      </div>
    </section>
  );
}

function printSaleInvoice(sale: SaleResponse, company: CompanyProfile = fallbackCompany) {
  const businessName = company.commercialName || company.name;
  const rows = sale.items
    .map(
      (item) => `
        <tr>
          <td>${escapeHtml(item.productName)}</td>
          <td class="right">${Number(item.quantity).toFixed(2)}</td>
          <td class="right">${money.format(Number(item.unitPrice))}</td>
          <td class="right">${money.format(Number(item.lineTotal))}</td>
        </tr>
      `
    )
    .join('');

  const payments = sale.payments
    .map((payment) => {
      const processor = payment.processor ? ` (${paymentProcessorLabels[payment.processor]})` : '';
      const reference = payment.reference ? ` - ${escapeHtml(payment.reference)}` : '';
      return `${paymentLabels[payment.method]}${processor}: ${money.format(Number(payment.amount))}${reference}`;
    })
    .join('<br />');

  const html = `
    <!doctype html>
    <html lang="es">
      <head>
        <meta charset="utf-8" />
        <title>${escapeHtml(sale.invoiceNumber)}</title>
        <style>
          * { box-sizing: border-box; }
          body {
            color: #17212b;
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 28px;
          }
          .invoice {
            margin: 0 auto;
            max-width: 760px;
          }
          .header {
            align-items: flex-start;
            border-bottom: 2px solid #17212b;
            display: flex;
            justify-content: space-between;
            padding-bottom: 16px;
          }
          h1, h2, p { margin: 0; }
          h1 { font-size: 24px; }
          h2 { font-size: 18px; margin-bottom: 8px; }
          .muted { color: #5d6964; font-size: 13px; line-height: 1.5; }
          .meta { text-align: right; }
          .block { margin-top: 22px; }
          table {
            border-collapse: collapse;
            margin-top: 12px;
            width: 100%;
          }
          th, td {
            border-bottom: 1px solid #dce3de;
            font-size: 13px;
            padding: 10px 8px;
            text-align: left;
          }
          th { background: #f3f5f1; color: #3e4a45; }
          .right { text-align: right; }
          .totals {
            margin-left: auto;
            margin-top: 18px;
            width: 280px;
          }
          .totals div {
            display: flex;
            justify-content: space-between;
            padding: 7px 0;
          }
          .total {
            border-top: 2px solid #17212b;
            font-size: 18px;
            font-weight: 700;
          }
          .footer {
            border-top: 1px solid #dce3de;
            margin-top: 28px;
            padding-top: 12px;
          }
          @media print {
            body { padding: 0; }
          }
        </style>
      </head>
      <body>
        <main class="invoice">
          <section class="header">
            <div>
              <h1>${escapeHtml(businessName)}</h1>
              <p class="muted">
                ${company.rnc ? `RNC ${escapeHtml(company.rnc)}<br />` : ''}
                ${escapeHtml(company.address || 'Direccion no configurada')}<br />
                ${escapeHtml(company.phone || 'Telefono no configurado')}
              </p>
            </div>
            <div class="meta">
              <h2>Factura de venta</h2>
              <p class="muted">
                No. ${escapeHtml(sale.invoiceNumber)}<br />
                Fecha: ${new Date(sale.issuedAt).toLocaleString('es-DO')}<br />
                NCF: ${sale.ncf ? escapeHtml(sale.ncf) : 'Pendiente'}<br />
                e-CF: ${escapeHtml(ecfStatusLabels[sale.ecfStatus ?? 'NOT_SUBMITTED'])}
              </p>
            </div>
          </section>

          <section class="block">
            <h2>Cliente</h2>
            <p class="muted">
              ${escapeHtml(sale.customerName)}<br />
              ${sale.customerFiscalId ? `RNC/Cedula: ${escapeHtml(sale.customerFiscalId)}` : 'Cliente de contado'}
            </p>
          </section>

          <section class="block">
            <h2>Detalle</h2>
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th class="right">Cant.</th>
                  <th class="right">Precio</th>
                  <th class="right">Total</th>
                </tr>
              </thead>
              <tbody>${rows}</tbody>
            </table>
          </section>

          <section class="totals">
            <div><span>Subtotal</span><strong>${money.format(Number(sale.subtotal))}</strong></div>
            <div><span>ITBIS</span><strong>${money.format(Number(sale.taxTotal))}</strong></div>
            <div><span>Descuento</span><strong>${money.format(Number(sale.discountTotal))}</strong></div>
            <div class="total"><span>Total</span><strong>${money.format(Number(sale.total))}</strong></div>
          </section>

          <section class="footer muted">
            <strong>Pagos</strong><br />
            ${payments || 'Sin pago registrado'}<br /><br />
            Gracias por su compra.
          </section>
        </main>
      </body>
    </html>
  `;

  const frame = document.createElement('iframe');
  frame.style.position = 'fixed';
  frame.style.right = '0';
  frame.style.bottom = '0';
  frame.style.width = '0';
  frame.style.height = '0';
  frame.style.border = '0';
  document.body.appendChild(frame);

  const documentRef = frame.contentWindow?.document;
  if (!documentRef) {
    document.body.removeChild(frame);
    return;
  }

  documentRef.open();
  documentRef.write(html);
  documentRef.close();

  setTimeout(() => {
    frame.contentWindow?.focus();
    frame.contentWindow?.print();
    setTimeout(() => document.body.removeChild(frame), 1000);
  }, 250);
}

function escapeHtml(value: string) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

function formatDateTime(value: string) {
  return new Date(value).toLocaleString('es-DO', {
    dateStyle: 'short',
    timeStyle: 'short'
  });
}

function formatQuantity(value: number) {
  return new Intl.NumberFormat('es-DO', {
    maximumFractionDigits: 2
  }).format(Number(value));
}

function formatSignedQuantity(value: number) {
  const numericValue = Number(value);
  if (numericValue === 0) {
    return '0';
  }
  return `${numericValue > 0 ? '+' : ''}${formatQuantity(numericValue)}`;
}

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function quoteFormToRequest(form: QuoteFormState): QuoteRequest {
  return {
    customerName: form.customerName.trim(),
    customerFiscalId: optionalText(form.customerFiscalId),
    issueDate: form.issueDate,
    validUntil: form.validUntil,
    status: form.status,
    notes: optionalText(form.notes),
    items: form.items.map((item) => ({
      productId: optionalText(item.productId),
      productName: item.productName.trim(),
      quantity: Number(item.quantity),
      unitPrice: Number(item.unitPrice),
      taxRate: Number(item.taxRate)
    }))
  };
}

function quoteToRequest(quote: Quote, status = quote.status): QuoteRequest {
  return {
    customerName: quote.customerName,
    customerFiscalId: quote.customerFiscalId,
    issueDate: quote.issueDate,
    validUntil: quote.validUntil,
    status,
    notes: quote.notes,
    items: quote.items.map((item) => ({
      productId: item.productId,
      productName: item.productName,
      quantity: Number(item.quantity),
      unitPrice: Number(item.unitPrice),
      taxRate: Number(item.taxRate)
    }))
  };
}

function productMatchesSearch(product: Product, searchTerm: string) {
  if (!searchTerm) {
    return true;
  }

  return [
    product.name,
    product.sku,
    product.barcode,
    product.categoryName,
    product.brandName
  ].some((value) => normalizeSearchText(value).includes(searchTerm));
}

function customerMatchesSearch(customer: Customer, searchTerm: string) {
  if (!searchTerm) {
    return true;
  }

  return [
    customer.name,
    customer.fiscalId,
    customer.phone,
    customer.email
  ].some((value) => normalizeSearchText(value).includes(searchTerm));
}

function taxRateForCustomer(product: Product, customer?: Customer) {
  return isTaxExemptCustomer(customer) ? 0 : product.taxRate;
}

function isTaxExemptCustomer(customer?: Customer) {
  return customer?.fiscalProfile === 'FREE_ZONE'
    || customer?.fiscalProfile === 'GOVERNMENT'
    || customer?.fiscalProfile === 'SPECIAL_REGIME';
}

function normalizeSearchText(value?: string) {
  return (value ?? '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim();
}

function productToForm(product: Product): ProductFormState {
  return {
    id: product.id,
    sku: product.sku,
    barcode: product.barcode ?? '',
    name: product.name,
    description: product.description ?? '',
    categoryName: product.categoryName ?? '',
    brandName: product.brandName ?? '',
    unit: product.unit,
    costPrice: Number(product.costPrice),
    salePrice: Number(product.salePrice),
    taxRate: Number(product.taxRate),
    currentStock: Number(product.currentStock),
    minimumStock: Number(product.minimumStock),
    active: product.active
  };
}

function productFormToRequest(form: ProductFormState): ProductRequest {
  return {
    sku: form.sku.trim(),
    barcode: optionalText(form.barcode),
    name: form.name.trim(),
    description: optionalText(form.description),
    categoryName: optionalText(form.categoryName),
    brandName: optionalText(form.brandName),
    unit: form.unit.trim() || 'unidad',
    costPrice: Number(form.costPrice) || 0,
    salePrice: Number(form.salePrice) || 0,
    taxRate: Number(form.taxRate) || 0,
    currentStock: Number(form.currentStock) || 0,
    minimumStock: Number(form.minimumStock) || 0,
    active: form.active
  };
}

function movementFormToRequest(form: InventoryMovementFormState): InventoryMovementRequest {
  return {
    productId: form.productId,
    movementType: form.movementType,
    quantity: Number(form.quantity) || 0,
    unitCost: Number(form.unitCost) > 0 ? Number(form.unitCost) : undefined,
    reference: optionalText(form.reference),
    notes: optionalText(form.notes)
  };
}

function purchaseFormToRequest(form: PurchaseInvoiceFormState): PurchaseInvoiceRequest {
  return {
    purchaseOrderId: form.purchaseOrderId,
    supplierId: form.supplierId,
    documentNumber: form.documentNumber.trim(),
    invoiceDate: form.invoiceDate,
    dueDate: form.paymentTerm === 'CREDIT' ? optionalText(form.dueDate) : undefined,
    paymentTerm: form.paymentTerm,
    notes: optionalText(form.notes),
    items: form.items.map((item) => ({
      productId: item.productId,
      quantity: Number(item.quantity) || 0,
      unitCost: Number(item.unitCost) || 0,
      taxRate: Number(item.taxRate) || 0
    }))
  };
}

function purchaseOrderFormToRequest(form: PurchaseOrderFormState): PurchaseOrderRequest {
  return {
    supplierId: form.supplierId,
    expectedDate: optionalText(form.expectedDate),
    notes: optionalText(form.notes),
    items: form.items.map((item) => ({
      productId: item.productId,
      quantity: Number(item.quantity) || 0,
      unitCost: Number(item.unitCost) || 0,
      taxRate: Number(item.taxRate) || 0
    }))
  };
}

function countFormToRequest(form: InventoryCountFormState): InventoryCountRequest {
  return {
    notes: optionalText(form.notes),
    items: form.items.map((item) => ({
      productId: item.productId,
      countedStock: Number(item.countedStock) || 0
    }))
  };
}

function supplierToForm(supplier: Supplier): SupplierFormState {
  return {
    id: supplier.id,
    name: supplier.name,
    rnc: supplier.rnc ?? '',
    phone: supplier.phone ?? '',
    email: supplier.email ?? '',
    address: supplier.address ?? '',
    active: supplier.active
  };
}

function supplierFormToRequest(form: SupplierFormState): SupplierRequest {
  return {
    name: form.name.trim(),
    rnc: optionalText(form.rnc),
    phone: optionalText(form.phone),
    email: optionalText(form.email),
    address: optionalText(form.address),
    active: form.active
  };
}

function supplierToRequest(supplier: Supplier, active = supplier.active): SupplierRequest {
  return {
    name: supplier.name,
    rnc: optionalText(supplier.rnc),
    phone: optionalText(supplier.phone),
    email: optionalText(supplier.email),
    address: optionalText(supplier.address),
    active
  };
}

function customerToForm(customer: Customer): CustomerFormState {
  return {
    id: customer.id,
    name: customer.name,
    type: customer.type,
    fiscalProfile: customer.fiscalProfile ?? 'STANDARD',
    fiscalId: customer.fiscalId ?? '',
    phone: customer.phone ?? '',
    email: customer.email ?? '',
    address: customer.address ?? '',
    creditLimit: Number(customer.creditLimit),
    active: customer.active
  };
}

function customerFormToRequest(form: CustomerFormState): CustomerRequest {
  return {
    name: form.name.trim(),
    type: form.type,
    fiscalProfile: form.fiscalProfile,
    fiscalId: optionalText(form.fiscalId),
    phone: optionalText(form.phone),
    email: optionalText(form.email),
    address: optionalText(form.address),
    creditLimit: Number(form.creditLimit) || 0,
    active: form.active
  };
}

function customerToRequest(customer: Customer, active = customer.active): CustomerRequest {
  return {
    name: customer.name,
    type: customer.type,
    fiscalProfile: customer.fiscalProfile ?? 'STANDARD',
    fiscalId: optionalText(customer.fiscalId),
    phone: optionalText(customer.phone),
    email: optionalText(customer.email),
    address: optionalText(customer.address),
    creditLimit: Number(customer.creditLimit) || 0,
    active
  };
}

function createDefaultEmployeePermissions(): Record<EmployeeAccessModule, EmployeeAccessLevel> {
  return employeePermissionModules.reduce((permissions, module) => {
    permissions[module.key] = 'NONE';
    return permissions;
  }, {} as Record<EmployeeAccessModule, EmployeeAccessLevel>);
}

function createBlankEmployeeForm(): EmployeeFormState {
  return {
    firstName: '',
    lastName: '',
    documentId: '',
    position: '',
    department: '',
    phone: '',
    email: '',
    hireDate: '',
    salary: 0,
    commissionRate: 0,
    active: true,
    username: '',
    password: '',
    userActive: true,
    allowWebAccess: true,
    permissions: createDefaultEmployeePermissions()
  };
}

function employeeToForm(employee: Employee): EmployeeFormState {
  const permissions = createDefaultEmployeePermissions();
  employee.permissions?.forEach((permission) => {
    permissions[permission.module] = permission.accessLevel;
  });

  return {
    id: employee.id,
    firstName: employee.firstName,
    lastName: employee.lastName,
    documentId: employee.documentId ?? '',
    position: employee.position,
    department: employee.department ?? '',
    phone: employee.phone ?? '',
    email: employee.email ?? '',
    hireDate: employee.hireDate ?? '',
    salary: Number(employee.salary) || 0,
    commissionRate: Number(employee.commissionRate) || 0,
    active: employee.active,
    username: employee.username ?? '',
    password: '',
    userActive: employee.userActive,
    allowWebAccess: employee.allowWebAccess,
    permissions
  };
}

function employeeFormToRequest(form: EmployeeFormState): EmployeeRequest {
  const permissions: EmployeePermission[] = employeePermissionModules.map((module) => ({
    module: module.key,
    accessLevel: form.permissions[module.key]
  }));

  return {
    firstName: form.firstName.trim(),
    lastName: form.lastName.trim(),
    documentId: optionalText(form.documentId),
    position: form.position.trim(),
    department: optionalText(form.department),
    phone: optionalText(form.phone),
    email: optionalText(form.email),
    hireDate: optionalText(form.hireDate),
    salary: Number(form.salary) || 0,
    commissionRate: Number(form.commissionRate) || 0,
    active: form.active,
    username: optionalText(form.username),
    password: optionalText(form.password),
    userActive: form.userActive,
    allowWebAccess: form.allowWebAccess,
    permissions
  };
}

function optionalText(value?: string) {
  const cleanValue = value?.trim();
  return cleanValue ? cleanValue : undefined;
}

function BillingView({ apiOnline }: { apiOnline: boolean }) {
  const [sequences, setSequences] = useState<NcfSequence[]>([]);
  const [sales, setSales] = useState<SaleResponse[]>([]);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [processingSaleId, setProcessingSaleId] = useState<string | null>(null);

  const loadBilling = async () => {
    if (!apiOnline) {
      setSequences([]);
      setSales([]);
      setMessage('El backend debe estar conectado para consultar facturacion RD.');
      return;
    }

    setLoading(true);
    setMessage('');
    try {
      const [sequenceData, saleData] = await Promise.all([
        getNcfSequences(),
        getSales()
      ]);
      setSequences(sequenceData);
      setSales(saleData.slice(0, 20));
      if (sequenceData.length === 0) {
        setMessage('No hay secuencias NCF activas configuradas.');
      }
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo cargar facturacion RD.');
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateEcf = async (saleId: string) => {
    setProcessingSaleId(saleId);
    setMessage('');
    try {
      const document = await generateSaleEcf(saleId);
      setMessage(`XML e-CF simulado generado para ${document.invoiceNumber}. Hash: ${document.xmlHash?.slice(0, 16) ?? 'sin hash'}...`);
      await loadBilling();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo generar el XML e-CF simulado.');
    } finally {
      setProcessingSaleId(null);
    }
  };

  const handleSubmitSimulated = async (saleId: string) => {
    setProcessingSaleId(saleId);
    setMessage('');
    try {
      const document = await submitSaleEcfSimulation(saleId);
      setMessage(`Acuse simulado guardado. TrackId: ${document.trackId}. Codigo: ${document.securityCode}.`);
      await loadBilling();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar el acuse simulado.');
    } finally {
      setProcessingSaleId(null);
    }
  };

  useEffect(() => {
    void loadBilling();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiOnline]);

  return (
    <section className="content-grid">
      <div className="panel wide">
        <div className="panel-heading">
          <div>
            <h2>Facturacion RD</h2>
            <p>NCF, XML e-CF simulado, firma simulada y acuse de prueba listos para preparar la integracion real DGII.</p>
          </div>
          <button className="secondary-button" onClick={loadBilling} disabled={loading}>
            <RefreshCw size={17} />
            <span>{loading ? 'Cargando...' : 'Actualizar'}</span>
          </button>
        </div>

        {message && <div className="form-message">{message}</div>}

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Prefijo</th>
                <th>Proximo</th>
                <th>Hasta</th>
                <th>Vence</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {sequences.map((sequence) => (
                <tr key={sequence.id}>
                  <td>{sequence.documentType}</td>
                  <td>{sequence.prefix}</td>
                  <td>{sequence.currentNumber}</td>
                  <td>{sequence.endNumber}</td>
                  <td>{new Date(`${sequence.validUntil}T00:00:00`).toLocaleDateString('es-DO')}</td>
                  <td>
                    <span className={sequence.active ? 'status-badge active' : 'status-badge inactive'}>
                      {sequence.active ? 'Activa' : 'Inactiva'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="panel wide">
        <div className="panel-heading">
          <div>
            <h2>e-CF simulado por factura</h2>
            <p>Genera el XML, agrega firma de prueba y guarda un acuse local. No sustituye el certificado ni el envio oficial DGII.</p>
          </div>
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Factura</th>
                <th>NCF</th>
                <th>Cliente</th>
                <th>Total</th>
                <th>e-CF</th>
                <th>Track / codigo</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {sales.map((sale) => (
                <tr key={sale.id}>
                  <td>{sale.invoiceNumber}</td>
                  <td>{sale.ncf || 'Sin NCF'}</td>
                  <td>{sale.customerName || 'Cliente de contado'}</td>
                  <td>{money.format(Number(sale.total))}</td>
                  <td>{ecfStatusLabel(sale.ecfStatus)}</td>
                  <td>
                    {sale.ecfTrackId
                      ? `${sale.ecfTrackId} / ${sale.ecfSecurityCode ?? 'sin codigo'}`
                      : 'Pendiente'}
                  </td>
                  <td>
                    <div className="inline-actions">
                      <button
                        className="secondary-button"
                        onClick={() => handleGenerateEcf(sale.id)}
                        disabled={processingSaleId === sale.id}
                      >
                        <FileText size={15} />
                        <span>Generar XML</span>
                      </button>
                      <button
                        className="primary-button"
                        onClick={() => handleSubmitSimulated(sale.id)}
                        disabled={processingSaleId === sale.id}
                      >
                        <ShieldCheck size={15} />
                        <span>Enviar sim.</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {sales.length === 0 && (
                <tr>
                  <td colSpan={7}>No hay facturas recientes para preparar e-CF.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}

function ecfStatusLabel(status?: SaleResponse['ecfStatus']) {
  if (status === 'SIGNED') {
    return 'XML firmado sim.';
  }
  if (status === 'ACCEPTED') {
    return 'Aceptado sim.';
  }
  if (status === 'SUBMITTED') {
    return 'Enviado';
  }
  if (status === 'REJECTED') {
    return 'Rechazado';
  }
  if (status === 'READY_TO_SIGN') {
    return 'Listo para firma';
  }
  return 'Sin generar';
}

type AccountingTab = 'summary' | 'payments' | 'accounts' | 'journals' | 'entries' | 'manual' | 'trial';

type AccountingPaymentTarget = {
  type: 'receivable' | 'payable';
  document: AccountingDocument;
};

const accountingPaymentMethodLabels: Record<AccountingPaymentMethod, string> = {
  CASH: 'Efectivo',
  BANK_TRANSFER: 'Transferencia',
  CARD: 'Tarjeta',
  CHECK: 'Cheque',
  OTHER: 'Otro'
};

const accountingAccountTypes = [
  'ASSET',
  'RECEIVABLE',
  'BANK_CASH',
  'CURRENT_ASSET',
  'FIXED_ASSET',
  'PAYABLE',
  'LIABILITY',
  'EQUITY',
  'INCOME',
  'EXPENSE',
  'TAX'
];

const accountingJournalTypes = ['CUSTOMER_INVOICE', 'VENDOR_BILL', 'CASH', 'BANK', 'GENERAL'];

function defaultAccountingDate() {
  return new Date().toISOString().slice(0, 10);
}

function AccountingView({ apiOnline }: { apiOnline: boolean }) {
  const [summary, setSummary] = useState<AccountingSummary | null>(null);
  const [accounts, setAccounts] = useState<AccountingAccount[]>([]);
  const [journals, setJournals] = useState<AccountingJournal[]>([]);
  const [entries, setEntries] = useState<JournalEntry[]>([]);
  const [payments, setPayments] = useState<AccountingPayment[]>([]);
  const [trialBalance, setTrialBalance] = useState<TrialBalanceLine[]>([]);
  const [activeTab, setActiveTab] = useState<AccountingTab>('summary');
  const [paymentTarget, setPaymentTarget] = useState<AccountingPaymentTarget | null>(null);
  const [paymentForm, setPaymentForm] = useState<AccountingPaymentRequest>({
    amount: 0,
    paymentDate: defaultAccountingDate(),
    method: 'BANK_TRANSFER',
    journalCode: 'BAN',
    reference: '',
    notes: ''
  });
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const loadAccounting = async () => {
    if (!apiOnline) {
      setSummary(null);
      setAccounts([]);
      setJournals([]);
      setEntries([]);
      setPayments([]);
      setTrialBalance([]);
      setMessage('El backend debe estar conectado para consultar contabilidad.');
      return;
    }

    setLoading(true);
    setMessage('');
    try {
      const [summaryData, accountData, journalData, entryData, paymentData, trialData] = await Promise.all([
        getAccountingSummary(),
        getAccountingAccounts(),
        getAccountingJournals(),
        getJournalEntries(),
        getAccountingPayments(),
        getTrialBalance()
      ]);
      setSummary(summaryData);
      setAccounts(accountData);
      setJournals(journalData);
      setEntries(entryData);
      setPayments(paymentData);
      setTrialBalance(trialData);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo cargar contabilidad.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadAccounting();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiOnline]);

  const selectPaymentTarget = (target: AccountingPaymentTarget) => {
    setPaymentTarget(target);
    setPaymentForm({
      amount: Number(target.document.balance),
      paymentDate: defaultAccountingDate(),
      method: 'BANK_TRANSFER',
      journalCode: 'BAN',
      reference: target.document.documentNumber ?? '',
      notes: ''
    });
    setMessage('');
  };

  const submitPayment = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!paymentTarget) {
      return;
    }
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para registrar pagos.');
      return;
    }

    setLoading(true);
    setMessage('');
    try {
      const request = {
        ...paymentForm,
        amount: Number(paymentForm.amount),
        journalCode: paymentForm.journalCode || undefined,
        reference: optionalText(paymentForm.reference),
        notes: optionalText(paymentForm.notes)
      };
      if (paymentTarget.type === 'receivable') {
        await registerReceivablePayment(paymentTarget.document.id, request);
        setMessage('Cobro registrado, conciliado y contabilizado.');
      } else {
        await registerPayablePayment(paymentTarget.document.id, request);
        setMessage('Pago registrado, conciliado y contabilizado.');
      }
      setPaymentTarget(null);
      await loadAccounting();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo registrar el pago.');
    } finally {
      setLoading(false);
    }
  };

  const saveAccount = async (request: AccountingAccountRequest, id?: string) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar cuentas.');
      return;
    }
    setLoading(true);
    setMessage('');
    try {
      if (id) {
        await updateAccountingAccount(id, request);
        setMessage('Cuenta contable actualizada.');
      } else {
        await createAccountingAccount(request);
        setMessage('Cuenta contable creada.');
      }
      await loadAccounting();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar la cuenta.');
    } finally {
      setLoading(false);
    }
  };

  const saveJournal = async (request: AccountingJournalRequest, id?: string) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar diarios.');
      return;
    }
    setLoading(true);
    setMessage('');
    try {
      if (id) {
        await updateAccountingJournal(id, request);
        setMessage('Diario contable actualizado.');
      } else {
        await createAccountingJournal(request);
        setMessage('Diario contable creado.');
      }
      await loadAccounting();
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar el diario.');
    } finally {
      setLoading(false);
    }
  };

  const saveManualEntry = async (request: Parameters<typeof createManualJournalEntry>[0]) => {
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para crear asientos.');
      return;
    }
    setLoading(true);
    setMessage('');
    try {
      await createManualJournalEntry(request);
      setMessage('Asiento manual posteado.');
      await loadAccounting();
      setActiveTab('entries');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo crear el asiento.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section>
      <section className="stats-grid">
        <MetricCard label="Por cobrar" value={money.format(Number(summary?.receivablesBalance ?? 0))} tone="green" />
        <MetricCard label="Por pagar" value={money.format(Number(summary?.payablesBalance ?? 0))} tone="red" />
        <MetricCard label="Posicion neta" value={money.format(Number(summary?.netPosition ?? 0))} tone="blue" />
        <MetricCard label="Pagos conciliados" value={`${payments.length}`} tone="sand" />
      </section>

      <div className="inventory-tabs">
        {[
          ['summary', 'Resumen'],
          ['payments', 'Pagos'],
          ['accounts', 'Catalogo'],
          ['journals', 'Diarios'],
          ['entries', 'Asientos'],
          ['manual', 'Asiento manual'],
          ['trial', 'Balance']
        ].map(([key, label]) => (
          <button
            key={key}
            className={activeTab === key ? 'active' : ''}
            onClick={() => setActiveTab(key as typeof activeTab)}
          >
            {label}
          </button>
        ))}
      </div>

      <section className="content-grid">
        {activeTab === 'summary' && (
          <>
            <AccountingPaymentFormPanel
              target={paymentTarget}
              form={paymentForm}
              journals={journals}
              loading={loading}
              onChange={setPaymentForm}
              onCancel={() => setPaymentTarget(null)}
              onSubmit={submitPayment}
            />
            <AccountingDocumentPanel
              title="Cuentas por cobrar"
              kind="receivable"
              documents={summary?.receivables ?? []}
              loading={loading}
              onRefresh={loadAccounting}
              onRegisterPayment={(document) => selectPaymentTarget({ type: 'receivable', document })}
            />
            <AccountingDocumentPanel
              title="Cuentas por pagar"
              kind="payable"
              documents={summary?.payables ?? []}
              loading={loading}
              onRefresh={loadAccounting}
              onRegisterPayment={(document) => selectPaymentTarget({ type: 'payable', document })}
            />
          </>
        )}
        {activeTab === 'payments' && <AccountingPaymentsPanel payments={payments} loading={loading} onRefresh={loadAccounting} />}
        {activeTab === 'accounts' && <AccountingAccountsPanel accounts={accounts} loading={loading} onRefresh={loadAccounting} onSave={saveAccount} />}
        {activeTab === 'journals' && <AccountingJournalsPanel journals={journals} loading={loading} onRefresh={loadAccounting} onSave={saveJournal} />}
        {activeTab === 'entries' && <JournalEntriesPanel entries={entries} loading={loading} onRefresh={loadAccounting} />}
        {activeTab === 'manual' && <ManualJournalEntryPanel accounts={accounts} journals={journals} loading={loading} onSave={saveManualEntry} />}
        {activeTab === 'trial' && <TrialBalancePanel lines={trialBalance} loading={loading} onRefresh={loadAccounting} />}
      </section>
      {message && <div className="form-message">{message}</div>}
    </section>
  );
}

function AccountingPaymentFormPanel({
  target,
  form,
  journals,
  loading,
  onChange,
  onCancel,
  onSubmit
}: {
  target: AccountingPaymentTarget | null;
  form: AccountingPaymentRequest;
  journals: AccountingJournal[];
  loading: boolean;
  onChange: (form: AccountingPaymentRequest) => void;
  onCancel: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
}) {
  if (!target) {
    return (
      <div className="panel wide">
        <div className="panel-heading compact">
          <div>
            <h2>Pagos y conciliacion</h2>
            <p>Selecciona una cuenta por cobrar o pagar para registrar un cobro/pago y generar su asiento automatico.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>{target.type === 'receivable' ? 'Registrar cobro' : 'Registrar pago'}</h2>
          <p>{target.document.partyName} · {target.document.documentNumber || 'Sin documento'} · balance {money.format(Number(target.document.balance))}</p>
        </div>
        <button className="secondary-button" type="button" onClick={onCancel}>
          <X size={16} />
          <span>Cancelar</span>
        </button>
      </div>
      <form className="employee-form-grid" onSubmit={onSubmit}>
        <label>
          Monto
          <input
            type="number"
            min="0"
            step="0.01"
            value={form.amount}
            onChange={(event) => onChange({ ...form, amount: Number(event.target.value) })}
          />
        </label>
        <label>
          Fecha
          <input
            type="date"
            value={form.paymentDate}
            onChange={(event) => onChange({ ...form, paymentDate: event.target.value })}
          />
        </label>
        <label>
          Metodo
          <select
            value={form.method}
            onChange={(event) => {
              const method = event.target.value as AccountingPaymentMethod;
              onChange({
                ...form,
                method,
                journalCode: method === 'CASH' ? 'CAJ' : 'BAN'
              });
            }}
          >
            {Object.entries(accountingPaymentMethodLabels).map(([key, label]) => (
              <option key={key} value={key}>{label}</option>
            ))}
          </select>
        </label>
        <label>
          Diario
          <select value={form.journalCode ?? ''} onChange={(event) => onChange({ ...form, journalCode: event.target.value })}>
            <option value="">Automatico</option>
            {journals.map((journal) => (
              <option key={journal.id} value={journal.code}>{journal.code} · {journal.name}</option>
            ))}
          </select>
        </label>
        <label>
          Referencia
          <input value={form.reference ?? ''} onChange={(event) => onChange({ ...form, reference: event.target.value })} />
        </label>
        <label>
          Nota
          <input value={form.notes ?? ''} onChange={(event) => onChange({ ...form, notes: event.target.value })} />
        </label>
        <div className="customer-form-actions">
          <button className="primary-button" type="submit" disabled={loading}>
            <CheckCircle2 size={16} />
            <span>{target.type === 'receivable' ? 'Registrar cobro' : 'Registrar pago'}</span>
          </button>
        </div>
      </form>
    </div>
  );
}

function AccountingPaymentsPanel({
  payments,
  loading,
  onRefresh
}: {
  payments: AccountingPayment[];
  loading: boolean;
  onRefresh: () => void;
}) {
  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Pagos conciliados</h2>
          <p>Historial de cobros y pagos aplicados contra cuentas por cobrar o pagar.</p>
        </div>
        <button className="secondary-button" onClick={onRefresh} disabled={loading}>
          <RefreshCw size={17} />
          <span>Actualizar</span>
        </button>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Tipo</th>
              <th>Entidad</th>
              <th>Metodo</th>
              <th>Diario</th>
              <th>Monto</th>
              <th>Referencia</th>
            </tr>
          </thead>
          <tbody>
            {payments.map((payment) => (
              <tr key={payment.id}>
                <td>{new Date(`${payment.paymentDate}T00:00:00`).toLocaleDateString('es-DO')}</td>
                <td>{payment.direction === 'CUSTOMER' ? 'Cobro cliente' : 'Pago suplidor'}</td>
                <td>{payment.partyName}</td>
                <td>{accountingPaymentMethodLabels[payment.method]}</td>
                <td>{payment.journalCode}</td>
                <td>{money.format(Number(payment.amount))}</td>
                <td>{payment.reference || 'Sin referencia'}</td>
              </tr>
            ))}
            {payments.length === 0 && (
              <tr>
                <td colSpan={7}>Todavia no hay pagos conciliados.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function AccountingAccountsPanel({
  accounts,
  loading,
  onRefresh,
  onSave
}: {
  accounts: AccountingAccount[];
  loading: boolean;
  onRefresh: () => void;
  onSave: (request: AccountingAccountRequest, id?: string) => Promise<void>;
}) {
  const emptyForm: AccountingAccountRequest = {
    code: '',
    name: '',
    accountType: 'ASSET',
    normalBalance: 'DEBIT',
    allowReconciliation: false,
    active: true
  };
  const [form, setForm] = useState<AccountingAccountRequest>(emptyForm);
  const [editingId, setEditingId] = useState<string | undefined>();

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await onSave(form, editingId);
    setForm(emptyForm);
    setEditingId(undefined);
  };

  const editAccount = (account: AccountingAccount) => {
    setEditingId(account.id);
    setForm({
      code: account.code,
      name: account.name,
      accountType: account.accountType,
      normalBalance: account.normalBalance,
      allowReconciliation: account.allowReconciliation,
      active: account.active
    });
  };

  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Catalogo de cuentas</h2>
          <p>Cuentas base para mayor general, balance y reportes contables.</p>
        </div>
        <button className="secondary-button" onClick={onRefresh} disabled={loading}>
          <RefreshCw size={17} />
          <span>Actualizar</span>
        </button>
      </div>
      <form className="employee-form-grid" onSubmit={submit}>
        <label>
          Codigo
          <input value={form.code} onChange={(event) => setForm((current) => ({ ...current, code: event.target.value }))} />
        </label>
        <label>
          Nombre
          <input value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} />
        </label>
        <label>
          Tipo
          <select value={form.accountType} onChange={(event) => setForm((current) => ({ ...current, accountType: event.target.value }))}>
            {accountingAccountTypes.map((type) => <option key={type} value={type}>{type}</option>)}
          </select>
        </label>
        <label>
          Balance normal
          <select value={form.normalBalance} onChange={(event) => setForm((current) => ({ ...current, normalBalance: event.target.value as 'DEBIT' | 'CREDIT' }))}>
            <option value="DEBIT">Debito</option>
            <option value="CREDIT">Credito</option>
          </select>
        </label>
        <label>
          Conciliable
          <select value={form.allowReconciliation ? 'yes' : 'no'} onChange={(event) => setForm((current) => ({ ...current, allowReconciliation: event.target.value === 'yes' }))}>
            <option value="no">No</option>
            <option value="yes">Si</option>
          </select>
        </label>
        <label>
          Estado
          <select value={form.active ? 'active' : 'inactive'} onChange={(event) => setForm((current) => ({ ...current, active: event.target.value === 'active' }))}>
            <option value="active">Activa</option>
            <option value="inactive">Inactiva</option>
          </select>
        </label>
        <div className="customer-form-actions">
          <button className="primary-button" type="submit" disabled={loading}>
            <Save size={16} />
            <span>{editingId ? 'Actualizar cuenta' : 'Crear cuenta'}</span>
          </button>
          {editingId && (
            <button className="secondary-button" type="button" onClick={() => { setEditingId(undefined); setForm(emptyForm); }}>
              Cancelar
            </button>
          )}
        </div>
      </form>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Codigo</th>
              <th>Cuenta</th>
              <th>Tipo</th>
              <th>Balance normal</th>
              <th>Conciliable</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map((account) => (
              <tr key={account.id}>
                <td>{account.code}</td>
                <td>{account.name}</td>
                <td>{account.accountType}</td>
                <td>{account.normalBalance === 'DEBIT' ? 'Debito' : 'Credito'}</td>
                <td>{account.allowReconciliation ? 'Si' : 'No'}</td>
                <td>
                  <button className="secondary-button" onClick={() => editAccount(account)}>
                    <Edit3 size={15} />
                    <span>Editar</span>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function AccountingJournalsPanel({
  journals,
  loading,
  onRefresh,
  onSave
}: {
  journals: AccountingJournal[];
  loading: boolean;
  onRefresh: () => void;
  onSave: (request: AccountingJournalRequest, id?: string) => Promise<void>;
}) {
  const emptyForm: AccountingJournalRequest = {
    code: '',
    name: '',
    journalType: 'GENERAL',
    active: true
  };
  const [form, setForm] = useState<AccountingJournalRequest>(emptyForm);
  const [editingId, setEditingId] = useState<string | undefined>();

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await onSave(form, editingId);
    setForm(emptyForm);
    setEditingId(undefined);
  };

  const editJournal = (journal: AccountingJournal) => {
    setEditingId(journal.id);
    setForm({
      code: journal.code,
      name: journal.name,
      journalType: journal.journalType,
      active: journal.active
    });
  };

  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Diarios contables</h2>
          <p>Separan ventas, compras, caja, banco y operaciones varias.</p>
        </div>
        <button className="secondary-button" onClick={onRefresh} disabled={loading}>
          <RefreshCw size={17} />
          <span>Actualizar</span>
        </button>
      </div>
      <form className="employee-form-grid" onSubmit={submit}>
        <label>
          Codigo
          <input value={form.code} onChange={(event) => setForm((current) => ({ ...current, code: event.target.value }))} />
        </label>
        <label>
          Nombre
          <input value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} />
        </label>
        <label>
          Tipo
          <select value={form.journalType} onChange={(event) => setForm((current) => ({ ...current, journalType: event.target.value }))}>
            {accountingJournalTypes.map((type) => <option key={type} value={type}>{type}</option>)}
          </select>
        </label>
        <label>
          Estado
          <select value={form.active ? 'active' : 'inactive'} onChange={(event) => setForm((current) => ({ ...current, active: event.target.value === 'active' }))}>
            <option value="active">Activo</option>
            <option value="inactive">Inactivo</option>
          </select>
        </label>
        <div className="customer-form-actions">
          <button className="primary-button" type="submit" disabled={loading}>
            <Save size={16} />
            <span>{editingId ? 'Actualizar diario' : 'Crear diario'}</span>
          </button>
          {editingId && (
            <button className="secondary-button" type="button" onClick={() => { setEditingId(undefined); setForm(emptyForm); }}>
              Cancelar
            </button>
          )}
        </div>
      </form>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Codigo</th>
              <th>Diario</th>
              <th>Tipo</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {journals.map((journal) => (
              <tr key={journal.id}>
                <td>{journal.code}</td>
                <td>{journal.name}</td>
                <td>{journal.journalType}</td>
                <td>{journal.active ? 'Activo' : 'Inactivo'}</td>
                <td>
                  <button className="secondary-button" onClick={() => editJournal(journal)}>
                    <Edit3 size={15} />
                    <span>Editar</span>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function ManualJournalEntryPanel({
  accounts,
  journals,
  loading,
  onSave
}: {
  accounts: AccountingAccount[];
  journals: AccountingJournal[];
  loading: boolean;
  onSave: (request: Parameters<typeof createManualJournalEntry>[0]) => Promise<void>;
}) {
  const [form, setForm] = useState({
    journalCode: 'MISC',
    entryDate: defaultAccountingDate(),
    reference: '',
    notes: '',
    lines: [
      { accountCode: '', label: '', partnerName: '', debit: 0, credit: 0 },
      { accountCode: '', label: '', partnerName: '', debit: 0, credit: 0 }
    ]
  });

  const totalDebit = form.lines.reduce((sum, line) => sum + Number(line.debit || 0), 0);
  const totalCredit = form.lines.reduce((sum, line) => sum + Number(line.credit || 0), 0);

  const updateLine = (index: number, patch: Partial<(typeof form.lines)[number]>) => {
    setForm((current) => ({
      ...current,
      lines: current.lines.map((line, lineIndex) => lineIndex === index ? { ...line, ...patch } : line)
    }));
  };

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    await onSave({
      ...form,
      reference: optionalText(form.reference),
      notes: optionalText(form.notes),
      lines: form.lines.map((line) => ({
        accountCode: line.accountCode,
        label: line.label || 'Movimiento contable',
        partnerName: optionalText(line.partnerName),
        debit: Number(line.debit) || 0,
        credit: Number(line.credit) || 0
      }))
    });
    setForm({
      journalCode: 'MISC',
      entryDate: defaultAccountingDate(),
      reference: '',
      notes: '',
      lines: [
        { accountCode: '', label: '', partnerName: '', debit: 0, credit: 0 },
        { accountCode: '', label: '', partnerName: '', debit: 0, credit: 0 }
      ]
    });
  };

  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Asiento manual</h2>
          <p>Para ajustes, reclasificaciones y operaciones que no vienen de ventas/compras.</p>
        </div>
      </div>
      <form onSubmit={submit}>
        <div className="employee-form-grid">
          <label>
            Diario
            <select value={form.journalCode} onChange={(event) => setForm((current) => ({ ...current, journalCode: event.target.value }))}>
              {journals.map((journal) => (
                <option key={journal.id} value={journal.code}>{journal.code} · {journal.name}</option>
              ))}
            </select>
          </label>
          <label>
            Fecha
            <input type="date" value={form.entryDate} onChange={(event) => setForm((current) => ({ ...current, entryDate: event.target.value }))} />
          </label>
          <label>
            Referencia
            <input value={form.reference} onChange={(event) => setForm((current) => ({ ...current, reference: event.target.value }))} />
          </label>
          <label>
            Nota
            <input value={form.notes} onChange={(event) => setForm((current) => ({ ...current, notes: event.target.value }))} />
          </label>
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Cuenta</th>
                <th>Detalle</th>
                <th>Entidad</th>
                <th>Debito</th>
                <th>Credito</th>
              </tr>
            </thead>
            <tbody>
              {form.lines.map((line, index) => (
                <tr key={index}>
                  <td>
                    <select value={line.accountCode} onChange={(event) => updateLine(index, { accountCode: event.target.value })}>
                      <option value="">Seleccionar</option>
                      {accounts.map((account) => (
                        <option key={account.id} value={account.code}>{account.code} · {account.name}</option>
                      ))}
                    </select>
                  </td>
                  <td><input value={line.label} onChange={(event) => updateLine(index, { label: event.target.value })} /></td>
                  <td><input value={line.partnerName} onChange={(event) => updateLine(index, { partnerName: event.target.value })} /></td>
                  <td><input type="number" step="0.01" value={line.debit} onChange={(event) => updateLine(index, { debit: Number(event.target.value), credit: 0 })} /></td>
                  <td><input type="number" step="0.01" value={line.credit} onChange={(event) => updateLine(index, { credit: Number(event.target.value), debit: 0 })} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="customer-form-actions">
          <button className="secondary-button" type="button" onClick={() => setForm((current) => ({
            ...current,
            lines: [...current.lines, { accountCode: '', label: '', partnerName: '', debit: 0, credit: 0 }]
          }))}>
            <Plus size={16} />
            <span>Agregar linea</span>
          </button>
          <span>Debitos {money.format(totalDebit)} · Creditos {money.format(totalCredit)}</span>
          <button className="primary-button" type="submit" disabled={loading || totalDebit !== totalCredit || totalDebit <= 0}>
            <Save size={16} />
            <span>Postear asiento</span>
          </button>
        </div>
      </form>
    </div>
  );
}

function JournalEntriesPanel({
  entries,
  loading,
  onRefresh
}: {
  entries: JournalEntry[];
  loading: boolean;
  onRefresh: () => void;
}) {
  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Asientos contables</h2>
          <p>Movimientos posteados con debitos y creditos balanceados.</p>
        </div>
        <button className="secondary-button" onClick={onRefresh} disabled={loading}>
          <RefreshCw size={17} />
          <span>Actualizar</span>
        </button>
      </div>
      <div className="quote-list">
        {entries.map((entry) => (
          <article className="quote-row" key={entry.id}>
            <div>
              <strong>{entry.entryNumber} · {entry.journalCode}</strong>
              <span>{entry.reference || 'Sin referencia'} · {new Date(`${entry.entryDate}T00:00:00`).toLocaleDateString('es-DO')}</span>
            </div>
            <span>{money.format(Number(entry.totalDebit))}</span>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Cuenta</th>
                    <th>Detalle</th>
                    <th>Debito</th>
                    <th>Credito</th>
                  </tr>
                </thead>
                <tbody>
                  {entry.lines.map((line) => (
                    <tr key={line.id}>
                      <td>{line.accountCode} · {line.accountName}</td>
                      <td>{line.label}</td>
                      <td>{money.format(Number(line.debit))}</td>
                      <td>{money.format(Number(line.credit))}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </article>
        ))}
        {entries.length === 0 && <article className="quote-row">Todavia no hay asientos posteados.</article>}
      </div>
    </div>
  );
}

function TrialBalancePanel({
  lines,
  loading,
  onRefresh
}: {
  lines: TrialBalanceLine[];
  loading: boolean;
  onRefresh: () => void;
}) {
  const totalDebit = lines.reduce((sum, line) => sum + Number(line.debit), 0);
  const totalCredit = lines.reduce((sum, line) => sum + Number(line.credit), 0);
  const income = lines
    .filter((line) => line.accountType === 'INCOME')
    .reduce((sum, line) => sum + Math.abs(Number(line.credit) - Number(line.debit)), 0);
  const expenses = lines
    .filter((line) => line.accountType === 'EXPENSE')
    .reduce((sum, line) => sum + Math.abs(Number(line.debit) - Number(line.credit)), 0);
  const assets = lines
    .filter((line) => ['ASSET', 'BANK_CASH', 'RECEIVABLE', 'CURRENT_ASSET', 'FIXED_ASSET'].includes(line.accountType) || (line.accountType === 'TAX' && line.normalBalance === 'DEBIT'))
    .reduce((sum, line) => sum + Number(line.balance), 0);
  const liabilities = lines
    .filter((line) => ['PAYABLE', 'LIABILITY'].includes(line.accountType) || (line.accountType === 'TAX' && line.normalBalance === 'CREDIT'))
    .reduce((sum, line) => sum + Math.abs(Number(line.balance)), 0);
  const equity = lines
    .filter((line) => line.accountType === 'EQUITY')
    .reduce((sum, line) => sum + Math.abs(Number(line.balance)), 0);
  const netIncome = income - expenses;

  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Balance de comprobacion</h2>
          <p>Total debito {money.format(totalDebit)} · total credito {money.format(totalCredit)}.</p>
        </div>
        <button className="secondary-button" onClick={onRefresh} disabled={loading}>
          <RefreshCw size={17} />
          <span>Actualizar</span>
        </button>
      </div>
      <section className="stats-grid">
        <MetricCard label="Ingresos" value={money.format(income)} tone="green" />
        <MetricCard label="Gastos" value={money.format(expenses)} tone="red" />
        <MetricCard label="Resultado" value={money.format(netIncome)} tone="blue" />
        <MetricCard label="Activos" value={money.format(assets)} tone="sand" />
      </section>
      <div className="quote-list">
        <article className="quote-row">
          <strong>Balance general rapido</strong>
          <span>Activos {money.format(assets)} · Pasivos {money.format(liabilities)} · Capital {money.format(equity)} · Resultado {money.format(netIncome)}</span>
        </article>
        <article className="quote-row">
          <strong>Balance de comprobacion</strong>
          <span>Total debito {money.format(totalDebit)} · total credito {money.format(totalCredit)}</span>
        </article>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Cuenta</th>
              <th>Debito</th>
              <th>Credito</th>
              <th>Balance</th>
            </tr>
          </thead>
          <tbody>
            {lines.map((line) => (
              <tr key={line.accountCode}>
                <td>{line.accountCode} · {line.accountName}</td>
                <td>{money.format(Number(line.debit))}</td>
                <td>{money.format(Number(line.credit))}</td>
                <td>{money.format(Number(line.balance))}</td>
              </tr>
            ))}
            {lines.length === 0 && (
              <tr>
                <td colSpan={4}>Todavia no hay movimientos para el balance.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function AccountingDocumentPanel({
  title,
  kind,
  documents,
  loading,
  onRefresh,
  onRegisterPayment
}: {
  title: string;
  kind: 'receivable' | 'payable';
  documents: AccountingSummary['receivables'];
  loading: boolean;
  onRefresh: () => void;
  onRegisterPayment: (document: AccountingDocument) => void;
}) {
  return (
    <div className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>{title}</h2>
          <p>{documents.length} documentos recientes.</p>
        </div>
        <button className="secondary-button" onClick={onRefresh} disabled={loading}>
          <RefreshCw size={17} />
          <span>Actualizar</span>
        </button>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Entidad</th>
              <th>Documento</th>
              <th>Vence</th>
              <th>Balance</th>
              <th>Estado</th>
              <th>Accion</th>
            </tr>
          </thead>
          <tbody>
            {documents.map((document) => (
              <tr key={document.id}>
                <td>{document.partyName}</td>
                <td>{document.documentNumber || 'Sin numero'}</td>
                <td>{document.dueDate ? new Date(`${document.dueDate}T00:00:00`).toLocaleDateString('es-DO') : 'Sin fecha'}</td>
                <td>{money.format(Number(document.balance))}</td>
                <td>{document.status}</td>
                <td>
                  <button
                    className="primary-button"
                    onClick={() => onRegisterPayment(document)}
                    disabled={loading || Number(document.balance) <= 0 || document.status === 'PAID'}
                  >
                    <CreditCard size={15} />
                    <span>{kind === 'receivable' ? 'Cobrar' : 'Pagar'}</span>
                  </button>
                </td>
              </tr>
            ))}
            {documents.length === 0 && (
              <tr>
                <td colSpan={6}>No hay documentos pendientes.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function SettingsView({
  apiOnline,
  company,
  onCompanySaved
}: {
  apiOnline: boolean;
  company: CompanyProfile;
  onCompanySaved: (company: CompanyProfile) => void;
}) {
  const [form, setForm] = useState<CompanyRequest>(companyToRequest(company));
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    setForm(companyToRequest(company));
  }, [company]);

  const saveCompany = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!apiOnline) {
      setMessage('El backend debe estar conectado para guardar la empresa.');
      return;
    }

    setSaving(true);
    setMessage('');
    try {
      const saved = await updateCompanyProfile({
        ...form,
        taxRate: Number(form.taxRate) || 0
      });
      onCompanySaved(saved);
      setMessage('Datos de empresa guardados.');
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'No se pudo guardar la empresa.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="panel wide">
      <div className="panel-heading">
        <div>
          <h2>Datos de la empresa</h2>
          <p>Estos datos se usan en facturas, impresiones y adaptacion del sistema.</p>
        </div>
      </div>

      {message && <div className="form-message">{message}</div>}

      <form className="customer-editor-form" onSubmit={saveCompany}>
        <label>
          Nombre legal
          <input value={form.name} onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))} />
        </label>
        <label>
          Nombre comercial
          <input value={form.commercialName ?? ''} onChange={(event) => setForm((current) => ({ ...current, commercialName: event.target.value }))} />
        </label>
        <label>
          RNC
          <input value={form.rnc ?? ''} onChange={(event) => setForm((current) => ({ ...current, rnc: event.target.value }))} />
        </label>
        <label>
          Telefono
          <input value={form.phone ?? ''} onChange={(event) => setForm((current) => ({ ...current, phone: event.target.value }))} />
        </label>
        <label>
          Correo
          <input value={form.email ?? ''} onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))} type="email" />
        </label>
        <label>
          Moneda
          <input value={form.currencyCode} onChange={(event) => setForm((current) => ({ ...current, currencyCode: event.target.value }))} />
        </label>
        <label>
          ITBIS por defecto
          <input
            value={form.taxRate}
            onChange={(event) => setForm((current) => ({ ...current, taxRate: Number(event.target.value) }))}
            type="number"
            min="0"
            step="0.01"
          />
        </label>
        <label>
          Logo URL
          <input value={form.logoUrl ?? ''} onChange={(event) => setForm((current) => ({ ...current, logoUrl: event.target.value }))} />
        </label>
        <label className="span-2">
          Direccion
          <textarea value={form.address ?? ''} onChange={(event) => setForm((current) => ({ ...current, address: event.target.value }))} rows={3} />
        </label>
        <div className="customer-form-actions span-2">
          <button className="primary-button" type="submit" disabled={saving}>
            <Save size={17} />
            <span>{saving ? 'Guardando...' : 'Guardar empresa'}</span>
          </button>
        </div>
      </form>
    </section>
  );
}

function companyToRequest(company: CompanyProfile): CompanyRequest {
  return {
    name: company.name,
    commercialName: company.commercialName ?? '',
    rnc: company.rnc ?? '',
    phone: company.phone ?? '',
    email: company.email ?? '',
    address: company.address ?? '',
    logoUrl: company.logoUrl ?? '',
    currencyCode: company.currencyCode,
    taxRate: Number(company.taxRate)
  };
}

function ModulePlaceholder({ title }: { title: string }) {
  return (
    <section className="panel placeholder-panel">
      <h2>{title}</h2>
      <p>Modulo preparado para la siguiente fase del sistema.</p>
    </section>
  );
}

function MetricCard({ label, value, tone }: { label: string; value: string; tone: 'green' | 'blue' | 'sand' | 'red' }) {
  return (
    <article className={`metric-card ${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}
