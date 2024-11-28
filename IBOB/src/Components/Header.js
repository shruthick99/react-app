import React from 'react';
import './Header.css';
import { useNavigate } from 'react-router-dom';
import ameicoldLogo from '../assets/miracle-logo-dark.png';


const Header = () => {

    const nav = useNavigate();

    const onCheckInClick = () => {
        nav('/checkin-form')
    }
    const onQRCodeClick = () => {
        nav('/checkin-form/qr-code')
    }

    const checkInSystemHandler = () => {
        nav('/')
    }

    return (
        <header className="header">
            {/* <button onClick={checkInSystemHandler}> */}
            <div onClick={checkInSystemHandler} className='imgDiv'>
                <img src={ameicoldLogo} alt="Americold" className="logo1" />
            </div>
            {/* </button> */}
            <div className="header-buttons">
                <button onClick={onQRCodeClick} className="header-button"><b>QR Code</b></button>
                <button onClick={onCheckInClick} className="header-button"><b>Check-In Form</b></button>
            </div>
        </header>
    );
};

export default Header;
