import { useState, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../assets/logo.png';
import authService from '../../services/auth.service';

export default function Register() {
    const navigate = useNavigate();
    
    // Core State
    const [step, setStep] = useState(1);
    const [indexNumber, setIndexNumber] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    
    // OTP State
    const [otp, setOtp] = useState(['', '', '', '', '', '']);
    const inputRefs = useRef([]);

    // Password State
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    // --- Handlers ---

    // Step 1: Send Index Number to backend to generate OTP
    const handleContinueStep1 = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        
        try {
            await authService.initiateRegistration(indexNumber);
            setStep(2); // Move to OTP step on success
        } catch (err) {
            setError(err.response?.data?.message || "Failed to initiate registration. Please check your index number.");
        } finally {
            setLoading(false);
        }
    };

    // Step 2: User enters OTP and clicks continue (moves to password screen)
    const handleContinueStep2 = () => {
        // We verify the OTP on the final step alongside the password
        if (otp.join('').length !== 6) {
            setError("Please enter the full 6-digit OTP.");
            return;
        }
        setError('');
        setStep(3); 
    };

    // Step 3: Send Index Number, OTP, and Password to backend to finalize
    const handleSignUp = async (e) => {
        e.preventDefault();
        
        if (password !== confirmPassword) {
            setError("Passwords do not match!");
            return;
        }

        setLoading(true);
        setError('');
        
        try {
            const finalOtp = otp.join('');
            await authService.completeRegistration(indexNumber, finalOtp, password);
            
            // Success! Redirect the user to the login page
            alert("Registration successful! Please log in.");
            navigate('/login');
        } catch (err) {
            setError(err.response?.data?.message || "Registration failed. Your OTP might be invalid or expired.");
            // Optionally, send them back to step 2 if the OTP failed
            setStep(2);
        } finally {
            setLoading(false);
        }
    };

    // --- OTP Input Helpers ---
    const handleOtpChange = (index, value) => {
        if (isNaN(value)) return; // Only allow numbers
        const newOtp = [...otp];
        newOtp[index] = value;
        setOtp(newOtp);

        // Move to next input if a number is entered
        if (value !== '' && index < 5) {
            inputRefs.current[index + 1].focus();
        }
    };

    const handleOtpKeyDown = (index, e) => {
        // Move to previous input on backspace if current is empty
        if (e.key === 'Backspace' && index > 0 && otp[index] === '') {
            inputRefs.current[index - 1].focus();
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen px-4 py-12 font-sans bg-background sm:px-6 lg:px-8">
            <div className="w-full max-w-md p-6 transition-all duration-300 bg-background sm:p-10">
                
                {/* Logo Section */}
                <div className="flex justify-center mb-8">
                    <img src={logo} alt="NSPS Logo" className="object-contain h-20 sm:h-24" />
                </div>

                {/* Dynamic Divider based on Step */}
                <div className="flex items-center mb-6">
                    <div className="flex-grow border-t border-gray-200"></div>
                    <span className="px-4 text-sm font-medium text-gray-500">
                        {step === 1 && 'Sign Up'}
                        {step === 2 && 'Verify your index number'}
                        {step === 3 && 'Set your password'}
                    </span>
                    <div className="flex-grow border-t border-gray-200"></div>
                </div>

                {/* Error Message Display */}
                {error && (
                    <div className="p-3 mb-6 text-sm text-red-700 bg-red-100 border border-red-200 rounded-md animate-fade-in">
                        {error}
                    </div>
                )}

                {/* Step 1: Index Number Form */}
                {step === 1 && (
                    <form onSubmit={handleContinueStep1} className="space-y-6 animate-fade-in">
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

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full px-4 py-3 font-semibold text-white transition-all rounded-md bg-primary hover:opacity-90 disabled:opacity-50 active:scale-[0.98]"
                        >
                            {loading ? 'Processing...' : 'Continue'}
                        </button>
                    </form>
                )}

                {/* Step 2: OTP Verification Form */}
                {step === 2 && (
                    <div className="animate-fade-in">
                        <p className="mb-8 text-sm text-center text-gray-500">
                            We have sent a 6-digit code to your registered email
                        </p>
                        
                        {/* OTP Input Boxes */}
                        <div className="flex justify-between gap-2 mb-4 sm:gap-3">
                            {otp.map((digit, index) => (
                                <input
                                    key={index}
                                    ref={(el) => (inputRefs.current[index] = el)}
                                    type="text"
                                    maxLength={1}
                                    value={digit}
                                    onChange={(e) => handleOtpChange(index, e.target.value)}
                                    onKeyDown={(e) => handleOtpKeyDown(index, e)}
                                    className="w-10 h-12 text-lg font-semibold text-center border border-gray-300 rounded-md focus:border-primary focus:ring-2 focus:ring-primary focus:outline-none sm:w-12 sm:h-14 sm:text-xl"
                                />
                            ))}
                        </div>
                        
                        <p className="mb-8 text-xs font-medium text-center text-gray-600">
                            Enter the 6-digit code we sent to your email
                        </p>

                        <button
                            onClick={handleContinueStep2}
                            className="w-full px-4 py-3 font-semibold text-white transition-all rounded-md bg-primary hover:opacity-90 active:scale-[0.98]"
                        >
                            Continue
                        </button>
                    </div>
                )}

                {/* Step 3: Set Password Form */}
                {step === 3 && (
                    <form onSubmit={handleSignUp} className="space-y-6 animate-fade-in">
                        <div>
                            <label className="block mb-2 text-sm font-semibold text-left text-gray-700">
                                Password
                            </label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Create a strong password"
                                className="w-full px-4 py-3 text-gray-800 transition-colors border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent sm:text-sm"
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-2 text-sm font-semibold text-left text-gray-700">
                                Confirm Password
                            </label>
                            <input
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Confirm your password"
                                className="w-full px-4 py-3 text-gray-800 transition-colors border border-gray-300 rounded-md placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent sm:text-sm"
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full px-4 py-3 font-semibold text-white transition-all rounded-md bg-primary hover:opacity-90 active:scale-[0.98] disabled:opacity-50"
                        >
                            {loading ? 'Finalizing...' : 'Sign up'}
                        </button>
                    </form>
                )}

                {/* Footer Links */}
                <div className="mt-8 text-sm text-center text-gray-600">
                    Already have an account?{' '}
                    <Link to="/login" className="font-semibold transition-colors text-primary hover:underline hover:text-opacity-80">
                        Login
                    </Link>
                </div>

                {/* Disclaimer */}
                <div className="mt-12 text-xs leading-relaxed text-center text-gray-400 sm:mt-16">
                    By clicking 'Continue', you acknowledge that you have read<br className="hidden sm:block" />
                    and accept the <a href="#" className="font-medium transition-colors text-primary hover:underline hover:text-opacity-80">Terms of Service</a> and <a href="#" className="font-medium transition-colors text-primary hover:underline hover:text-opacity-80">Privacy Policy</a>.
                </div>
            </div>
        </div>
    );
}