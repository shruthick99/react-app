import React, { useEffect, useState } from 'react';
import './CheckInRecords.css';

const RecordCard = ({ id, name, phone, email, date, confirmationNumber, status }) => {
    const handleDownload = () => {
        // Construct the download URL
        const downloadUrl = `http://3.132.187.127:8080/download-file/${id}`;
        
        // Create a temporary anchor element
        // const link = document.createElement('a');
        // link.href = downloadUrl;
        // link.download = `Document_${id}.pdf`; // Optional: Specify a default filename
        // document.body.appendChild(link);
        // link.click(); // Trigger the download
        // document.body.removeChild(link); // Clean up the DOM
        window.open(downloadUrl, '_blank'); // Open the download in a new tab
    };
    return (
        <div className="record-card">
            <div className="record-info">
                <p><strong>Name:</strong> {name}</p>
                <p><strong>Phone:</strong> {phone}</p>
                <p><strong>Email:</strong> {email}</p>
                <p><strong>Date of Entry:</strong> {date}</p>
                <p><strong>Document:</strong> 
                    <a 
                        style={{ color: "#007bff", cursor: "pointer" }} 
                        onClick={handleDownload}
                    > 
                        <b>View Document</b>
                    </a>
                </p>
                <p><strong>Confirmation Number:</strong> {confirmationNumber.substring(0, 13)}</p>
            </div>
            <div className={`status ${status === 'C' ? 'complete' : 'pending'}`}>
                {status === 'C' ? 'Complete' : 'Pending'}
            </div>
        </div>
    );
};

const CheckInRecords = () => {
    const [inboundRecords, setInboundRecords] = useState([]);
    const [outboundRecords, setOutboundRecords] = useState([]);

    useEffect(() => {
        
        const token = localStorage.getItem('token'); // Retrieve token from localStorage
  
        if (!token) {
          throw new Error('No token found, please login');
        }

        // Fetch the data from the API
        fetch('http://18.223.106.60:8080/check-in',{
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`, // Add Authorization header with the token
              'Content-Type': 'application/json',
            },  
        })
            .then((response) => response.json())
            .then((data) => {
                // Filter the data into inbound and outbound records
                const inbound = data.data.filter(record => record.checkinType === 'I');
                const outbound = data.data.filter(record => record.checkinType === 'O');
                // Set the records to the state
                setInboundRecords(inbound);
                setOutboundRecords(outbound);
            })
            .catch((error) => {
                console.error('Error fetching the records:', error);
            });
    }, []);

    return (
        <div className="records-container">
            <div className="record-section">
                <h2>Inbound Records</h2>
                {inboundRecords.length > 0 ? (
                    inboundRecords.map((record, index) => (
                        <RecordCard
                            key={index}
                            id={record.id} // Pass the ID for downloading the document
                            name={record.name}
                            phone={record.phoneNumber}
                            email={record.email}
                            date={record.createdDate}
                            confirmationNumber={record.confirmationNumber}
                            status={record.status}
                        />
                    ))
                ) : (
                    <p>No inbound records available.</p>
                )}
            </div>
            <div className="record-section">
                <h2>Outbound Records</h2>
                {outboundRecords.length > 0 ? (
                    outboundRecords.map((record, index) => (
                        <RecordCard
                            key={index}
                            id={record.id} // Pass the ID for downloading the document
                            name={record.name}
                            phone={record.phoneNumber}
                            email={record.email}
                            date={record.createdDate}
                            confirmationNumber={record.confirmationNumber}
                            status={record.status}
                        />
                    ))
                ) : (
                    <p>No outbound records available.</p>
                )}
            </div>
        </div>
    );
};

export default CheckInRecords;
