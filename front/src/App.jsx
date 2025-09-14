import { Routes, Route } from 'react-router-dom';
import { Layout, ConfigProvider } from 'antd';
import './App.css';
import Sidebar from './components/Sidebar';
import DynamicFeed from './components/DynamicFeed';
import IntelligentArchive from './components/IntelligentArchive';
import Dashboard from './components/Dashboard';
import CaseManagement from './components/CaseManagement';
import Calendar from './components/Calendar';
import ClientManagement from './components/ClientManagement';
import DocumentManagement from './components/DocumentManagement';

const { Content } = Layout;

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
        },
      }}
    >
      <div className="app-layout">
        <Sidebar />
        <div className="main-content">
          <div className="app-content">
            <Routes>
              <Route path="/" element={<DynamicFeed />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/cases" element={<CaseManagement />} />
              <Route path="/archive" element={<IntelligentArchive />} />
              <Route path="/calendar" element={<Calendar />} />
              <Route path="/clients" element={<ClientManagement />} />
              <Route path="/documents" element={<DocumentManagement />} />
            </Routes>
          </div>
        </div>
      </div>
    </ConfigProvider>
  );
}

export default App;
