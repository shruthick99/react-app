import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './CheckInInformation.css';
import { jwtDecode } from 'jwt-decode';

const CheckInInformation = () => {
    const { confirmationNumber } = useParams();
    const [data, setData] = useState(null);
    const [status, setStatus] = useState('Pending');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    console.log(confirmationNumber);

    const token = localStorage.getItem('token'); // Retrieve token from localStorage

    useEffect(() => {
        const fetchData = async () => {
            try {

                if (!token) {
                    throw new Error('No token found, please login');
                }

                const response = await fetch(`http://3.132.187.127:8080/check-in/${confirmationNumber}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`, // Add Authorization header with the token
                        'Content-Type': 'application/json',
                    },
                });
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const result = await response.json();
                if (result.success) {
                    setData(result.data);
                    setStatus(result.data.checkInStatus); // Set the status from the fetched data
                } else {
                    throw new Error(result.message);
                }
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [confirmationNumber]);

    const handleStatusChange = (e) => {
        setStatus(e.target.value);
        console.log(e.target.value);
    };

    const handleUpdateStatus = async () => {

        const decodedToken = jwtDecode(token);
        console.log(decodedToken);

        try {
            const response = await fetch(`http://3.132.187.127:8080/check-in/${data.id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`, // Add Authorization header with the token
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: decodedToken.loginId, // Ensure this is available in the fetched data
                    checkInStatus: status
                }),
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const result = await response.json();
            if (result.success) {
                alert(`Status updated to: ${status}`);
                navigate('/dashboard')
            } else {
                throw new Error(result.message);
            }
        } catch (error) {
            alert(`Error updating status: ${error.message}`);
        }
    };


    const backHandler = () => {
        navigate(-1);
    };

    //   if (loading) return <p>Loading...</p>;
    //   if (error) return <p>Error: {error}</p>;

    return (
        <div className="checkin-container">
            <button style={{ textAlign: "left", backgroundColor: "#007bff" }} onClick={backHandler}>
                <strong> &lt; Back </strong>
            </button>
            <div className="title">
                <h1>Check-In #{data?.checkinId} - <span className="inbound">{data?.checkinType == 'I' ? "Inbound" : "Outbound"}</span></h1>
            </div>
            {console.log(data)}
            <div className="info-section">
                <div className="row-1">
                    <div className="box driver-info">
                        <div className="boxHeader" style={{ height: "25%" }}>
                            <b style={{ marginLeft: "5%" }}>Driver Information</b>
                        </div>
                        {/* <div className="driver-info-grid">
                            <p><strong>Name:</strong> {data?.name}</p>
                            <p><strong>Phone:</strong> {data?.phoneNumber}</p>
                            <p><strong>License:</strong> {data?.license}</p>
                            <p><strong>Truck No:</strong> {data?.truckNo}</p>
                            <p><strong>Check-In ID:</strong> {data?.checkinId}</p>
                            <p><strong>Status:</strong> {data?.status}</p>
                            <p><strong>Date:</strong> {data?.createdDate}</p>
                            <p><strong>Confirmation No#:</strong> {data?.confirmationNumber.substring(0, 13)}</p>
                        </div> */}
                        <div style={{ display: "flex", justifyContent: "space-around", textAlign: "left" }}>
                            {/* Facility Column */}
                            <div style={{ flex: 1, marginLeft: "6%" }}>
                                <p><strong>Name:</strong> {data?.name}</p>
                                <p><strong>License:</strong> Ap39DH3048</p>
                                <p><strong>Check-In ID:</strong> {data?.checkinId}</p>
                                <p><strong>Date:</strong> {data?.createdDate}</p>
                            </div>

                            {/* Customer Column */}
                            <div style={{ flex: 1, marginLeft: "6%" }}>
                                <p><strong>Phone:</strong> {data?.phoneNumber}</p>
                                <p><strong>Truck No:</strong> Ap39DH3048</p>
                                <p><strong>Confirmation No#:</strong> {data?.confirmationNumber}</p>
                            </div>
                        </div>

                    </div>

                    <div className="box update-status">
                        <div className="boxHeader" style={{ height: "24%" }}>
                            <b style={{ marginLeft: "7%" }}>Update Status</b>
                        </div>
                        <p style={{ textAlign: "left", marginLeft: "6%" }}>Status</p>
                        {
                            data?.status == "P" ? (
                                <>
                                    <div style={{ display: "flex", justifyContent: "center" }}>
                                        <select value={status} onChange={handleStatusChange} style={{ width: "90%" }}>
                                            <option value="P">Pending</option>
                                            <option value="C">Complete</option>
                                            {/* <option value="Cancelled">Cancelled</option> */}
                                        </select>
                                    </div>
                                    <button onClick={handleUpdateStatus} style={{ width: "35%", marginLeft: "6%" }}>
                                        Update Status
                                    </button>
                                </>
                            ) : (
                                <>
                                    <h2 className='completeStatus'
                                    >Complete</h2>
                                </>
                            )
                        }
                    </div>
                </div>

                <div className="row-2">
                    <div className="box appointment-info">
                        <div className="boxHeader1" style={{ height: "17%" }}>
                            <b style={{ marginLeft: "7%" }}>Appointment Information</b>
                        </div>
                        <div style={{ textAlign: "left", marginLeft: "10%" }}>
                            <p><strong>Check-In ID:</strong> {data?.checkinId}</p>
                            <p><strong>Status:</strong> {data?.status === "P" ? "Pending" : "Complete"}</p>
                            <p><strong>Date:</strong> {data?.createdDate}</p>
                        </div>
                    </div>

                    <div className="box trip-info">
                        <div className="boxHeader1" style={{ height: "20%" }}>
                            <b style={{ marginLeft: "5%" }}>Trip Information</b>
                        </div>
                        <div style={{ display: "flex", justifyContent: "space-around", textAlign: "left" }}>
                            {/* Facility Column */}
                            <div style={{ flex: 1, marginLeft: "6%" }}>
                                <p>Facility</p>
                                <p><strong>Name:</strong> SKOLA</p>
                                <p><strong>ID:</strong> 5467</p>
                                <p><strong>SSC:</strong> 85%</p>
                            </div>

                            {/* Customer Column */}
                            <div style={{ flex: 1, marginLeft: "6%" }}>
                                <p>Customer</p>
                                <p><strong>Name:</strong> Raju</p>
                                <p><strong>ID:</strong> 1243</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CheckInInformation;
