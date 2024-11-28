import React, { useState } from 'react';
import './LoginPage.css'; // Import the CSS file
import Footer from './Footer';
import { useNavigate } from 'react-router-dom';
import Header from './Header';
import ameicoldLogo from '../assets/miracle-logo-dark.png';

const LoginPage = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const loginHandler = async (e) => {
        e.preventDefault();

        // Check for minimum length
        if (username.length < 3) {
            setError('Username must be at least 3 characters long.');
            return;
        }
        if (password.length < 3) {
            setError('Password must be at least 3 characters long.');
            return;
        }

        // Clear the error message if inputs are valid
        setError('');

        try {
            const response = await fetch('http://3.132.187.127:8080/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    loginId: username,
                    password: password,
                }),
            });

            const data = await response.json();

            if (data.success) {
                // Store the token in localStorage
                localStorage.setItem('token', data.token);
                navigate('/dashboard');
            } else {
                setError(data.message || 'Invalid credentials. Please try again.');
            }
        } catch (error) {
            console.error('Error during login:', error);
            setError('An error occurred. Please try again later.');
        }
    };

    return (
        <>
            <Header />
            <div className="login-container">
                <div>
                    <h1>Back Office for IOB</h1>
                </div>
                <div className="login-box">
                    <img src={ameicoldLogo} alt="Americold" className="logo" />
                    <h2>Login</h2>
                    <form onSubmit={loginHandler}>
                        <label htmlFor="username"><b>Username:</b></label>
                        <input
                            type="text"
                            id="username"
                            name="username"
                            placeholder="Enter username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            maxLength={60} // Restrict username to 60 characters
                        />

                        <label htmlFor="password"><b>Password:</b></label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            placeholder="Enter password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            maxLength={200} // Restrict password to 200 characters
                        />
                        <button type="submit">Login</button>
                    </form>
                    {error && <p className="error-message">{error}</p>}
                </div>
            </div>
            <Footer />
        </>
    );
};

export default LoginPage;
