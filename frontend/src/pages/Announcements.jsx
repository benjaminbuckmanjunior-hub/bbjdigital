import { useEffect, useState } from 'react';
import { getAnnouncements } from '../services/api';
import Layout from '../layouts/Layout';

export default function Announcements() {
    const [announcements, setAnnouncements] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getAnnouncements().then(response => {
            setAnnouncements(response.data || []);
            setLoading(false);
        }).catch(err => {
            console.error('Error fetching announcements:', err);
            setLoading(false);
        });
    }, []);

    if (loading) return <Layout><div className="text-center py-8 text-tealDeep font-semibold">Loading...</div></Layout>;

    return (
        <Layout>
            <div className="max-w-4xl mx-auto py-8">
                <h1 className="text-4xl font-bold mb-2 text-tealDeep">📢 Church Announcements</h1>
                <p className="text-gray-600 mb-8">Latest news and updates from BBJ Church</p>
                {announcements.length === 0 ? (
                    <div className="bg-yellow-50 border-l-4 border-lemon p-8 rounded-lg text-center">
                        <p className="text-gray-600 text-lg">No announcements yet.</p>
                        <p className="text-gray-500 mt-2">Check back soon for updates from the church!</p>
                    </div>
                ) : (
                    <div className="space-y-4">
                        {announcements.map((announcement, index) => (
                            <div key={announcement.id || index} className="bg-white p-6 rounded-lg border-l-4 border-lemon shadow-md hover:shadow-lg transition">
                                <h3 className="text-2xl font-semibold text-tealDeep mb-3">{announcement.title}</h3>
                                <p className="text-gray-700">{announcement.message}</p>
                                <p className="text-sm text-gray-500 mt-4">{new Date(announcement.createdAt).toLocaleDateString()}</p>
                                {announcement.fileUrl && (
                                    <div className="mt-4 pt-4 border-t border-gray-200">
                                        <a
                                            href={announcement.fileUrl}
                                            download
                                            className="inline-block bg-tealDeep text-white px-4 py-2 rounded font-semibold hover:bg-teal-700 transition text-sm"
                                        >
                                            📥 Download File
                                        </a>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </Layout>
    );
}

