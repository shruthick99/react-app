import React, { useState } from "react";
import "./BackOffice.css";
import { jwtDecode } from "jwt-decode";

const BackOffice = () => {

    const [confirmationNmbr, setConfirmationNmbr] = useState("");
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [email, setEmail] = useState("");
    const [appointmentTime, setAppointmentTime] = useState("");
    const [status, setStatus] = useState("P");
    const [noRecords, setNoRecords] = useState(false);
    const [showDetails, setShowDetails] = useState(false);
    const [data, setData] = useState(null);
    const [checkInRecords, setCheckInRecords] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const token = localStorage.getItem('token'); // Retrieve token from localStorage


    const handleSearch = async () => {
        setLoading(true);
        setError(null);
        try {
  
            if (!token) {
              throw new Error('No token found, please login');
            }
    
            const response = await fetch(`http://3.132.187.127:8080/check-in/${confirmationNmbr}`,{
                headers: {
                    'Authorization': `Bearer ${token}`, // Add Authorization header with the token
                    'Content-Type': 'application/json',
                  },        
            });
            if (response.ok) {
                const result = await response.json();
                console.log(result);

                if (result.success && result.data) {
                    const data = result.data;

                    if (data.confirmationNumber && data.confirmationNumber.toString() === confirmationNmbr) {
                        setData(data);
                        setName(data.name);
                        setPhone(data.phoneNumber);
                        setEmail(data.email);
                        setAppointmentTime(data.createdDate); // Adjust according to API response
                        setStatus(data.status === "P" ? "Pending" : "Completed"); // Adjust status mapping as needed
                        setNoRecords(false);
                        setShowDetails(false); // Hide details initially
                        setCheckInRecords([data]); // Wrap the record in an array to render the table
                    } else {
                        clearFields();
                        setNoRecords(true);
                        setShowDetails(false);
                    }
                } else {
                    clearFields();
                    setNoRecords(true);
                    setShowDetails(false);
                }
            } else {
                clearFields();
                setNoRecords(true);
                setShowDetails(false);
            }
        } catch (error) {
            console.error("Error fetching data:", error);
            clearFields();
            setNoRecords(true);
            setShowDetails(false);
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const clearFields = () => {
        setName("");
        setPhone("");
        setEmail("");
        setAppointmentTime("");
        setStatus("P");
        setCheckInRecords([]); // Clear the records as well
    };

    const handleSave = async () => {

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
                setShowDetails(false);
                handleSearch()
            } else {
                throw new Error(result.message);
            }
        } catch (error) {
            alert(`Error updating status: ${error.message}`);
        }
    };


    const handleRowClick = () => {
        setShowDetails(true);
    };

    return (
        <div className="back-office-container">
            <h1>Back Office</h1>
            <div className="search-section">
                <label htmlFor="confirmationNumber">Search by Confirmation Number:</label>
                <input
                    type="text"
                    id="confirmationNumber"
                    value={confirmationNmbr}
                    onChange={(e) => setConfirmationNmbr(e.target.value)}
                    onFocus={() => setShowDetails(false)}
                    required
                />
                <button onClick={handleSearch}>Search</button>
            </div>
            {noRecords && <p>No records found for the provided confirmation number.</p>}

            {!showDetails && checkInRecords.length > 0 && (
                <div className="record">
                    <table>
                        <thead>
                            <tr className="tableHeader">
                                <th>Name</th>
                                <th>Type</th>
                                <th>Status</th>
                                <th>Check-In ID</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="5">Loading...</td>
                                </tr>
                            ) : error ? (
                                <tr>
                                    <td colSpan="5">Error: {error}</td>
                                </tr>
                            ) : checkInRecords.length > 0 ? (
                                checkInRecords.map((record) => (
                                    <tr key={record.id} onClick={handleRowClick}>
                                        <td style={{ textAlign: "left" }}>
                                            <a style={{ color: "#007bff", cursor: "pointer" }}>
                                                {record.name}
                                            </a>
                                        </td>
                                        <td>{record.checkinType === 'I' ? 'Inbound' : 'Outbound'}</td>
                                        <td>{record.status === 'P'||'Pending' ? 'Pending' : 'Completed'}</td>
                                        <td>{record.checkinId}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="5">No records available</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            )}


            {showDetails && (
                <div className="form-section">
                    <div className="row">
                        <div className="input-group">
                            <label>Name:</label>
                            <input
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                disabled
                            />
                        </div>
                        <div className="input-group">
                            <label>Phone:</label>
                            <input
                                type="text"
                                value={phone}
                                onChange={(e) => setPhone(e.target.value)}
                                disabled
                            />
                        </div>
                        <div className="input-group">
                            <label>Email:</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled
                            />
                        </div>
                    </div>
                    <div className="row">
                        <div className="input-group">
                            <label>Appointment Time:</label>
                            <input
                                type="text"
                                value={appointmentTime}
                                onChange={(e) => setAppointmentTime(e.target.value)}
                                disabled
                            />
                        </div>
                        <div className="input-group">
                            <label>Status:</label>
                            {data?.status === "P" ? (
                                <div style={{ display: "flex", justifyContent: "center" }}>
                                    <select value={status} onChange={(e) => setStatus(e.target.value)}>
                                        <option value="P">Pending</option>
                                        <option value="C">Completed</option>
                                    </select>
                                </div>
                            ) : (
                                <h2 style={{ color: "green", textAlign: "justify", marginTop: "auto" }}>
                                    Completed
                                </h2>
                            )}
                        </div>
                    </div>
                    {data?.status === "P" && (
                        <button className="save-button" onClick={handleSave}>Save</button>
                    )}
                </div>
            )}
        </div>
    );
};

export default BackOffice;
