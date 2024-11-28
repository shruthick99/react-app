import React, { useRef, useState, useEffect } from 'react';
import './HeaderIOB.css'; // Ensure this CSS file exists for header-specific styles
import logout from '../assets/logout.png';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

const HeaderIOB = () => {
  const [showLogout, setShowLogout] = useState(false);
  const profileRef = useRef(null);
  const navigate = useNavigate();
  const [username, setUsername] = useState('');

  useEffect(() => {
    // Retrieve token from localStorage
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/');
    } else {
      try {
        // Decode the JWT to get the username
        const decodedToken = jwtDecode(token);
        console.log(decodedToken);
        setUsername(decodedToken.loginId); // Assuming the username is stored in `loginId`
      } catch (error) {
        console.error('Error decoding token:', error);
        navigate('/');
      }
    }

    // Redirect to login if no user data in localStorage
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [navigate]);

  const handleClickOutside = (event) => {
    if (profileRef.current && !profileRef.current.contains(event.target)) {
      setShowLogout(false);
    }
  };

  const handleIconClick = () => {
    setShowLogout((prevState) => !prevState);
  };

  const logoutHandler = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  const dashboardHandler = () => {
    navigate('/dashboard');
  };

  const recoredsHandler = () => {
    navigate('/dashboard/checkInRecords');
  };

  const backOfficeHandler = () => {
    navigate('/dashboard/backOffice');
  };

  return (
    <header className="header">
      <div className="header-buttons">
        <button className="header-button" onClick={dashboardHandler}><b>Dashboard</b></button>
        <button className="header-button" onClick={recoredsHandler}><b>Check-In Records</b></button>
        <button className="header-button" onClick={backOfficeHandler}><b>Back Office</b></button>
      </div>
      <div className="profile-container">
        <span className="username">{username}</span>
        <div className="profile-icon" ref={profileRef} onClick={handleIconClick}>
          <img src={logout} alt="Profile" />
          {showLogout && (
            <button className="logout-button" onClick={logoutHandler}>Logout</button>
          )}
        </div>
      </div>
    </header>
  );
};

export default HeaderIOB;
