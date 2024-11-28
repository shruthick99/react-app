import React from 'react';
import Header from './Header';
import { Outlet } from 'react-router-dom';
import Footer from './Footer';
import './Layout.css'; // Import the CSS file for styling
import CheckInForm from './CheckInForm';
import QrCode from './QrCode';

function Layout() {
  return (
    <>
      {/* <Header />
      <div className="layout-content">
        <div className="outlet-container">
          <Outlet/>
        </div>
      </div>
      <Footer /> */}


      <div className="dashboard" style={{ marginTop: "3%", marginBottom: "5%" }}>
        <Header />
        <Outlet />
        <Footer />
      </div>

    </>
  );
}

export default Layout;
