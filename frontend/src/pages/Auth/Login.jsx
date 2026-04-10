import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../assets/logo.png';
import authService from '../../services/auth.service';

export default function Login() {
    const navigate = useNavigate();
    const [indexNumber, setIndexNumber] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(''); // Clear any previous errors

        try {
            // Call your Spring Boot backend
            const response = await authService.login(indexNumber, password);

            // Extract the user's role from the backend response.
            // Converting to lowercase ensures we don't get tripped up by "Admin" vs "admin"
            const userRole = response.user.role?.toLowerCase() || 'student';

            // Route them based on their exact role
            if (userRole === 'superadmin') {
                navigate('/superadmin-dashboard');
            } else if (userRole === 'admin') {
                navigate('/admin-dashboard');
            } else {
                navigate('/student-dashboard'); // Default fallback
            }

        } catch (err) {
            // Display the error message from the backend, or a generic fallback
            setError(err.response?.data?.message || "Invalid index number or password.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen px-4 py-12 font-sans bg-background sm:px-6 lg:px-8">
            <div className="w-full max-w-md p-6 transition-all duration-300 bg-background sm:p-10">

                {/* Logo */}
                <div className="flex justify-center mb-8">
                    <img src={logo} alt="NSPS Logo" className="object-contain h-20 sm:h-24" />
                </div>

                {/* Divider */}
                <div className="flex items-center mb-8">
                    <div className="flex-grow border-t border-gray-200"></div>
                    <span className="px-4 text-sm font-medium text-gray-500">Log In</span>
                    <div className="flex-grow border-t border-gray-200"></div>
                </div>

                {/* Error Message Display */}
                {error && (
                    <div className="p-3 mb-6 text-sm text-red-700 bg-red-100 border border-red-200 rounded-md animate-fade-in">
                        {error}
                    </div>
                )}

                {/* Login Form */}
                <form onSubmit={handleLogin} className="space-y-6">
                    <div>
                        <label className="block mb-2 text-sm font-semibold text-left text-gray-700">
                            Index Number
                        </label>
                        <input
                            type="text"
                            value={indexNumber}
                            onChange={(e) => setIndexNumber(e.target.value)}
                            placeholder="Enter your index number"
                            className="w-full px-4 py-3 text-gray-800 transition-colors border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent sm:text-sm"
                            required
                        />
                    </div>

                    <div>
                        <label className="block mb-2 text-sm font-semibold text-left text-gray-700">
                            Password
                        </label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter your password"
                            className="w-full px-4 py-3 text-gray-800 transition-colors border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent sm:text-sm"
                            required
                        />
                        <div className="flex justify-end mt-2">
                            <a href="#" className="text-sm font-semibold transition-colors text-primary hover:underline hover:text-opacity-80">
                                Forgot Password?
                            </a>
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full px-4 py-3 mt-4 font-semibold text-white transition-all rounded-md bg-primary hover:opacity-90 active:scale-[0.98] disabled:opacity-50"
                    >
                        {loading ? 'Logging in...' : 'Log In'}
                    </button>
                </form>

                {/* Footer Link */}
                <div className="mt-8 text-sm text-center text-gray-600">
                    Do not have an account?{' '}
                    <Link to="/register" className="font-semibold transition-colors text-primary hover:underline hover:text-opacity-80">
                        Signup
                    </Link>
                </div>
            </div>
        </div>
    );
}