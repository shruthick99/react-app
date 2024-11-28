import React, { useState } from 'react';
import './CheckInForm.css';

const CheckInForm = () => {
    const [selectedTab, setSelectedTab] = useState('inbound');
    const [formData, setFormData] = useState({
        name: '',
        phoneNumber: '',
        email: '',
        checkInId: '',
        document: null,
    });
    const [formErrors, setFormErrors] = useState({});
    const [isSuccessModalVisible, setIsSuccessModalVisible] = useState(false);
    const [isErrorModalVisible, setIsErrorModalVisible] = useState(false);
    const [checkInResponse, setCheckInResponse] = useState()
    const [isLoading, setIsLoading] = useState(false); // New loading state

    const handleChange = (e) => {
        const { name, value, type, files } = e.target;

        // Prevent non-numeric characters in phoneNumber field
        if (name === 'phoneNumber' && value && !/^\d*$/.test(value)) {
            return; // If value is not numeric, stop further processing
        }
        setFormData(prevState => ({
            ...prevState,
            [name]: type === 'file' ? files[0] : value
        }));
    };

    const validateForm = () => {
        let errors = {};

        if (!formData.name.trim()) {
            errors.name = 'Name is required';
        } else if (!/^[A-Za-z\s]+$/.test(formData.name)) {
            errors.name = 'Name must contain only alphabetic characters';
        } else if (formData.name.length < 2) {
            errors.name = 'Name must be at least 2 characters';
        }

        if (!formData.phoneNumber.trim()) {
            errors.phoneNumber = 'Phone number is required';
        } else if (!/^\d{10}$/.test(formData.phoneNumber)) {
            errors.phoneNumber = 'Phone number must be a valid 10-digit number';
        }

        if (!formData.email.trim()) {
            errors.email = 'Email is required';
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            errors.email = 'Email must be a valid email address';
        }

        if (!formData.checkInId.trim()) {
            errors.checkInId = 'Check-In ID is required';
        } else if (!/^AMC-\d{3}$/.test(formData.checkInId)) {
            errors.checkInId = 'Check-In ID must be in the format "AMC-001"';
        }

        if (formData.document && !/\.(pdf|doc|docx|png|jpg)$/i.test(formData.document.name)) {
            errors.document = 'Only .pdf, .doc, .docx, .png, .jpg files are allowed';
        }

        setFormErrors(errors);

        // Return true if no errors
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setIsLoading(true); // Start loading

        const data = new FormData();
        data.append('name', formData.name);
        data.append('phoneNumber', formData.phoneNumber);
        data.append('email', formData.email);
        data.append('checkinId', formData.checkInId);
        data.append('checkinType', selectedTab === 'inbound' ? 'I' : 'O');
        if (formData.document) {
            data.append('document', formData.document);
        }

        try {
            const response = await fetch('http://3.132.187.127:8080/check-in-form', {
                method: 'POST',
                body: data,
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const result = await response.json();
            console.log('Success:', result);
            setCheckInResponse(result);
            setIsSuccessModalVisible(true);
            setFormData({
                name: '',
                phoneNumber: '',
                email: '',
                checkInId: '',
                document: null,
            });
        } catch (error) {
            console.error('Error:', error);
            setIsErrorModalVisible(true);
        } finally {
            setIsLoading(false); // End loading
        }
    };


    const closeModal = () => {
        setIsSuccessModalVisible(false);
        setIsErrorModalVisible(false);
    };

    return (
        <>
            <div className="checkin-form-container">
                <h2>Check-In Form</h2>
                <div className="tab-container">
                    <button
                        className={`tab ${selectedTab === 'inbound' ? 'active' : ''}`}
                        onClick={() => setSelectedTab('inbound')}
                    >
                        Inbound
                    </button>
                    <button
                        className={`tab ${selectedTab === 'outbound' ? 'active' : ''}`}
                        onClick={() => setSelectedTab('outbound')}
                    >
                        Outbound
                    </button>
                </div>
                <form className="checkin-form" onSubmit={handleSubmit}>
                    <label><b>Name:</b></label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        placeholder="Enter your name"
                    />
                    {formErrors.name && <span className="error">{formErrors.name}</span>}

                    <label><b>Phone:</b></label>
                    <input
                        type="text"
                        name="phoneNumber"
                        value={formData.phoneNumber}
                        onChange={handleChange}
                        placeholder="Enter your phone number"
                    />
                    {formErrors.phoneNumber && <span className="error">{formErrors.phoneNumber}</span>}

                    <label><b>Email:</b></label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        placeholder="Enter your email"
                    />
                    {formErrors.email && <span className="error">{formErrors.email}</span>}

                    <label><b>Check-In ID:</b></label>
                    <input
                        type="text"
                        name="checkInId"
                        value={formData.checkInId}
                        onChange={handleChange}
                        placeholder="Enter check-in ID"
                    />
                    {formErrors.checkInId && <span className="error">{formErrors.checkInId}</span>}

                    <label><b>Upload Document:</b></label>
                    <input
                        type="file"
                        name="document"
                        onChange={handleChange}
                    />
                    {formErrors.document && <span className="error">{formErrors.document}</span>}


                    {isLoading ? (
                        <div className="loader-container">
                            <div className="loader"></div>
                        </div>
                    ) : (
                        <button type="submit" style={{ marginTop: "5%" }} disabled={isLoading}>
                            <b>Check In</b>
                        </button>

                    )}

                </form>

                {/* Success Modal */}
                {isSuccessModalVisible && (
                    <div className="modal">
                        <div className="modal-content success">
                            <h3>Success</h3>
                            <p>Thank you for the Check-In! Here is your confirmation Number : <b>{checkInResponse.confNumber}</b></p>
                            <button onClick={closeModal}>Close</button>
                        </div>
                    </div>
                )}

                {/* Error Modal */}
                {isErrorModalVisible && (
                    <div className="modal">
                        <div className="modal-content error">
                            <h3>Error</h3>
                            <p>There was an issue with your check-in. Please try again.</p>
                            <button onClick={closeModal}>Close</button>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
};

export default CheckInForm;
