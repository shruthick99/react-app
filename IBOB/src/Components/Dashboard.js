import React, { useEffect, useState } from 'react';
import './Dashboard.css'; // Ensure this file exists and is properly styled
import Footer from './Footer';
import HeaderIOB from './HeaderIOB';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
  const [checkInRecords, setCheckInRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const [checkInRecords1, setCheckInRecords1] = useState([]);


  useEffect(() => {
    const fetchCheckInRecords = async () => {
      try {
        const token = localStorage.getItem('token'); // Retrieve token from localStorage
  
        if (!token) {
          throw new Error('No token found, please login');
        }
  
        const response = await fetch('http://3.132.187.127:8080/check-in', {
          method: 'GET',
          
          headers: {
            'Authorization': `Bearer ${token}`, // Add Authorization header with the token
            'Content-Type': 'application/json',
          },
        });
  
        if (!response.ok) {
          throw new Error('Failed to fetch check-in records');
        }
  
        const result = await response.json();
        if (result.success) {
          setCheckInRecords(result.data); // Store the check-in records
        } else {
          throw new Error(result.message);
        }
      } catch (error) {
        console.error('Error fetching check-in records:', error.message);
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };
  
    fetchCheckInRecords();
  }, []);


  // Calculate stats based on the fetched records
  const inboundCount = checkInRecords.filter(record => record.checkinType === 'I').length;
  const outboundCount = checkInRecords.filter(record => record.checkinType === 'O').length;
  const waitingCount = checkInRecords.filter(record => record.status === 'P').length;
  const totalCheckIns = checkInRecords.length;


  const handleViewClick = (confirmationNumber) => {
    navigate(`/dashboard/${confirmationNumber}`);
  };


  return (
    <div className="dashboard">
      <main className="main">
        <div className="stats">
          <div className="stat-box">
            <div className="stat-label"><b>Waiting List</b></div>
            <div className="stat-value"><span>{waitingCount}</span></div>
          </div>
          <div className="stat-box">
            <div className="stat-label"><b>Inbound</b></div>
            <div className="stat-value"><span>{inboundCount}</span></div>
          </div>
          <div className="stat-box">
            <div className="stat-label"><b>Outbound</b></div>
            <div className="stat-value"><span>{outboundCount}</span></div>
          </div>
          <div className="stat-box">
            <div className="stat-label"><b>Total CheckIns</b></div>
            <div className="stat-value"><span>{totalCheckIns}</span></div>
          </div>
        </div>
        <div className="records">
          <h2 className='recordsTitle'>All Check-In Records</h2>
          <table style={{ height: "10%" }}>
            <thead>
              <tr className="tableHeader">
                <th>Name</th>
                <th>Type</th>
                <th>Status</th>
                <th>Check-In ID</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {loading && (
                <tr>
                  <td colSpan="5">Loading...</td>
                </tr>
              )}
              {error && !loading && (
                <tr>
                  <td colSpan="5">Error: {error}</td>
                </tr>
              )}
              {!loading && !error && checkInRecords.length === 0 && (
                <tr>
                  <td colSpan="5">No records found</td>
                </tr>
              )}
              {!loading && !error && checkInRecords.map(record => (
                <tr key={record.id}>
                  <td style={{ textAlign: "left" }}>{record.name}</td>
                  <td>{record.checkinType === 'I' ? 'Inbound' : 'Outbound'}</td>
                  <td>{record.status === 'P' ? 'Pending' : 'Complete'}</td>
                  <td>{record.checkinId}</td>
                  <td style={{ textAlign: "center" }}><button onClick={() => handleViewClick(record.confirmationNumber)} >View</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
