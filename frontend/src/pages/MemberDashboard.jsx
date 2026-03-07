import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../layouts/Layout';

export default function MemberDashboard() {
    const navigate = useNavigate();
    const [memberData, setMemberData] = useState(null);
    const [announcements, setAnnouncements] = useState([]);
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const userType = localStorage.getItem('userType');
        const userId = localStorage.getItem('userId');
        const memberEmail = localStorage.getItem('memberEmail');

        if (userType !== 'member' || !userId) {
            navigate('/login');
            return;
        }

        // Get member data
        setMemberData({
            email: memberEmail,
            userId: userId
        });

        // Fetch announcements
        const fetchAnnouncements = async () => {
            try {
                const response = await fetch('/api/announcements');
                if (response.ok) {
                    const data = await response.json();
                    setAnnouncements((data.data || data).slice(0, 5));
                }
            } catch (err) {
                console.error('Error fetching announcements:', err);
            }
        };

        // Fetch events
        const fetchEvents = async () => {
            try {
                const response = await fetch('/api/events');
                if (response.ok) {
                    const data = await response.json();
                    setEvents((data.data || data).slice(0, 5));
                }
            } catch (err) {
                console.error('Error fetching events:', err);
            }
        };

        fetchAnnouncements();
        fetchEvents();
        setLoading(false);
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem('userId');
        localStorage.removeItem('userType');
        localStorage.removeItem('memberEmail');
        navigate('/login');
    };

    if (loading) {
        return (
            <Layout>
                <div className="min-h-screen flex items-center justify-center">
                    <p className="text-gray-600">Loading...</p>
                </div>
            </Layout>
        );
    }

    return (
        <Layout>
            <div className="min-h-screen bg-gray-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                    {/* Header */}
                    <div className="mb-8">
                        <div className="flex justify-between items-center">
                            <div>
                                <h1 className="text-4xl font-bold text-tealDeep mb-2">Member Dashboard</h1>
                                <p className="text-gray-600">Welcome to your church community portal</p>
                            </div>
                            <button
                                onClick={handleLogout}
                                className="bg-red-500 text-white px-6 py-2 rounded-lg hover:bg-red-600 transition font-semibold"
                            >
                                Logout
                            </button>
                        </div>
                    </div>

                    {/* Member Info Card */}
                    {memberData && (
                        <div className="bg-white rounded-lg shadow-lg p-6 mb-8 border-l-4 border-lemon">
                            <h2 className="text-xl font-bold text-tealDeep mb-4">Your Account</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <p className="text-gray-600 text-sm">Member ID</p>
                                    <p className="text-lg font-semibold text-tealDeep">{memberData.userId}</p>
                                </div>
                                <div>
                                    <p className="text-gray-600 text-sm">Email</p>
                                    <p className="text-lg font-semibold text-tealDeep break-all">{memberData.email}</p>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Quick Links */}
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                        <a
                            href="/announcements"
                            className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition border-t-4 border-lemon cursor-pointer"
                        >
                            <div className="text-3xl mb-3">📣</div>
                            <h3 className="text-xl font-bold text-tealDeep mb-2">Announcements</h3>
                            <p className="text-gray-600">Stay updated with church news and updates</p>
                        </a>

                        <a
                            href="/events"
                            className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition border-t-4 border-lemon cursor-pointer"
                        >
                            <div className="text-3xl mb-3">🗓️</div>
                            <h3 className="text-xl font-bold text-tealDeep mb-2">Events</h3>
                            <p className="text-gray-600">Browse upcoming church events and activities</p>
                        </a>

                        <a
                            href="/sermons"
                            className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition border-t-4 border-lemon cursor-pointer"
                        >
                            <div className="text-3xl mb-3">🎙️</div>
                            <h3 className="text-xl font-bold text-tealDeep mb-2">Sermons</h3>
                            <p className="text-gray-600">Listen to our latest sermons and teachings</p>
                        </a>
                    </div>

                    {/* Announcements Section */}
                    {announcements.length > 0 && (
                        <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
                            <div className="flex justify-between items-center mb-6">
                                <h2 className="text-2xl font-bold text-tealDeep">Latest Announcements</h2>
                                <a href="/announcements" className="text-lemon hover:text-tealDeep font-semibold transition">
                                    View All →
                                </a>
                            </div>
                            <div className="space-y-4">
                                {announcements.map((announcement) => (
                                    <div key={announcement.id} className="border-l-4 border-lemon pl-4 py-2">
                                        <h3 className="font-bold text-tealDeep">{announcement.title}</h3>
                                        <p className="text-gray-600 text-sm mt-1">{announcement.message}</p>
                                        {announcement.created_date && (
                                            <p className="text-gray-500 text-xs mt-2">
                                                {new Date(announcement.created_date).toLocaleDateString()}
                                            </p>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Events Section */}
                    {events.length > 0 && (
                        <div className="bg-white rounded-lg shadow-lg p-6">
                            <div className="flex justify-between items-center mb-6">
                                <h2 className="text-2xl font-bold text-tealDeep">Upcoming Events</h2>
                                <a href="/events" className="text-lemon hover:text-tealDeep font-semibold transition">
                                    View All →
                                </a>
                            </div>
                            <div className="space-y-4">
                                {events.map((event) => (
                                    <div key={event.id} className="border-l-4 border-lemon pl-4 py-2">
                                        <h3 className="font-bold text-tealDeep">{event.title}</h3>
                                        <p className="text-gray-600 text-sm mt-1">{event.description}</p>
                                        {event.event_date && (
                                            <p className="text-gray-500 text-xs mt-2">
                                                📅 {new Date(event.event_date).toLocaleDateString()}{' '}
                                                {new Date(event.event_date).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                            </p>
                                        )}
                                        {event.location && (
                                            <p className="text-gray-500 text-xs">📍 {event.location}</p>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </Layout>
    );
}
