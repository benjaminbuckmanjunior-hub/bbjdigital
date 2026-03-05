import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register } from '../services/api';
import Layout from '../layouts/Layout';

export default function Register() {
    const [formData, setFormData] = useState({
        name: ''
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        

        try {
            // send only what backend expects; extras are ignored
            const response = await register({ name: formData.name });
            if (response.data?.success) {
                const generatedEmail = response.data.email;
                // optionally save credentials
                localStorage.setItem('memberEmail', generatedEmail);
                // auto-login as member
                if (response.data.userId) {
                    localStorage.setItem('userId', response.data.userId);
                    localStorage.setItem('userType', 'member');
                }
                alert(`WELCOME TO BBJ DIGITAL CHURCH SYSTEM. YOUR LOGIN EMAIL IS "${generatedEmail}" USE THIS ANYTIME LOGGING IN`);
                // navigate to home/dashboard
                navigate('/home');
            } else {
                setError(response.data?.message || 'Registration failed');
            }
        } catch (err) {
            setError(err.message || 'Network error');
        }
    };

    return (
        <Layout>
            <div className="min-h-screen flex items-center justify-center py-12 px-4">
                <div className="w-full max-w-md">
                    {/* Header */}
                    <div className="text-center mb-8">
                        <div className="h-16 w-16 bg-lemon rounded-full flex items-center justify-center mx-auto mb-4 shadow-lg">
                            <span className="text-3xl font-bold text-tealDeep">+</span>
                        </div>
                        <h1 className="text-4xl font-bold text-tealDeep mb-2">Join BBJ Church</h1>
                        <p className="text-gray-600">Create your member account</p>
                    </div>

                    {/* Card */}
                    <div className="bg-white rounded-xl shadow-xl p-8 border-t-4 border-lemon">
                        {error && (
                            <div className="mb-6 p-4 bg-red-100 text-red-700 rounded-lg border border-red-200">
                                {error}
                            </div>
                        )}

                        <form onSubmit={handleSubmit} className="space-y-4">
                            <div>
                                <label className="block text-sm font-semibold text-tealDeep mb-2">Full Name</label>
                                <input 
                                    type="text"
                                    name="name"
                                    value={formData.name}
                                    onChange={handleChange}
                                    required
                                    className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-tealDeep focus:ring-1 focus:ring-lemon transition"
                                    placeholder="John Doe"
                                />
                            </div>

                            <p className="text-gray-600 text-sm">
                                An email will be automatically generated for you after registration. Please save it for logging in.
                            </p>

                            <button 
                                type="submit"
                                className="w-full bg-lemon text-tealDeep font-bold py-3 rounded-lg hover:bg-yellow-400 transition shadow-lg mt-6"
                            >
                                Create Account
                            </button>
                        </form>

                        <div className="mt-6 pt-6 border-t border-gray-200">
                            <p className="text-center text-gray-600 text-sm">
                                Already have an account? <a href="/login" className="text-tealDeep font-semibold hover:text-lemon transition">Login here</a>
                            </p>
                        </div>
                    </div>

                    {/* Footer Info */}
                    <p className="text-center text-gray-500 text-xs mt-6">© 2026 BBJ Church Manager</p>
                </div>
            </div>
        </Layout>
    );
}

