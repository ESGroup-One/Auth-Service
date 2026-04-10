import axios from 'axios';

// Assuming Spring Boot is running on its default port 8080
const API_URL = 'http://localhost:8080/api/auth';

const initiateRegistration = async (indexNumber) => {
    // Matches: @PostMapping("/register/initiate/{indexNumber}")
    const response = await axios.post(`${API_URL}/register/initiate/${indexNumber}`);
    return response.data;
};

const completeRegistration = async (indexNumber, otp, password) => {
    // Matches: @PostMapping("/register/complete")
    const response = await axios.post(`${API_URL}/register/complete`, {
        indexNumber,
        otp,
        password
    });
    return response.data;
};

const login = async (indexNumber, password) => {
    // Matches: @PostMapping("/login")
    const response = await axios.post(`${API_URL}/login`, {
        indexNumber,
        password
    });
    
    // Save the JWT token to local storage so the user stays logged in
    if (response.data.token) {
        localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
};

const logout = () => {
    localStorage.removeItem('user');
};

export default {
    initiateRegistration,
    completeRegistration,
    login,
    logout
};