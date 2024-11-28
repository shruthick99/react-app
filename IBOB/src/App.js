import logo from './logo.svg';
import './App.css';
import { useState } from 'react';
import CheckInForm from './Components/CheckInForm';
import Header from './Components/Header';
import Footer from './Components/Footer';
import { Route, Routes } from 'react-router-dom';
import Layout from './Components/Layout';
import QrCode from './Components/QrCode';
import LoginPage from './Components/LoginPage';
import Dashboard from './Components/Dashboard';
import CheckInInformation from './Components/CheckInInformation';
import LayoutIOB from './Components/LayoutIOB';
import CheckInRecords from './Components/CheckInRecords';
import BackOffice from './Components/BackOffice';

function App() {
  const [state, setState] = useState(false);
  const ShowHandler = () => {
    setState(true)
  }
  return (
    <div className="App">
      <Routes>
        <Route path='/' element={<LoginPage />} />
        <Route path='/dashboard' element={<LayoutIOB />}>
          <Route path='/dashboard' element={<Dashboard />} />
          <Route path='/dashboard/:confirmationNumber' element={<CheckInInformation />} />
          <Route path='/dashboard/checkInRecords' element={<CheckInRecords />} />
          <Route path='/dashboard/backOffice' element={<BackOffice />} />
        </Route>

        
        <Route path='/checkin-form' element={<Layout />}>
          <Route path='/checkin-form' element={<CheckInForm />} />
          <Route path='/checkin-form/qr-code' element={<QrCode />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
