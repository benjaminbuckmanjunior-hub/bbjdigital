import { useEffect, useState } from 'react';
import { getSermons } from '../services/api';
import Layout from '../layouts/Layout';

export default function Sermons() {
    const [sermons, setSermons] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getSermons().then(response => {
            setSermons(response.data || []);
            setLoading(false);
        }).catch(err => {
            console.error('Error fetching sermons:', err);
            setLoading(false);
        });
    }, []);

    if (loading) return <Layout><div className="text-center py-8 text-tealDeep font-semibold">Loading...</div></Layout>;

    return (
        <Layout>
            <div className="max-w-4xl mx-auto py-8">
                <h1 className="text-4xl font-bold mb-2 text-tealDeep">🎤 Sermon Library</h1>
                <p className="text-gray-600 mb-8">Watch and listen to our latest sermons</p>
                {sermons.length === 0 ? (
                    <div className="bg-yellow-50 border-l-4 border-lemon p-8 rounded-lg text-center">
                        <p className="text-gray-600 text-lg">No sermons available yet.</p>
                        <p className="text-gray-500 mt-2">New sermons will be added soon. Stay tuned!</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {sermons.map((sermon, index) => (
                            <div key={sermon.id || index} className="bg-white p-6 rounded-lg border-l-4 border-lemon shadow-md hover:shadow-lg transition">
                                <h3 className="text-2xl font-semibold text-tealDeep mb-2">{sermon.title}</h3>
                                <p className="text-gray-700 mb-4">{sermon.description}</p>
                                <p className="text-sm text-gray-600 mb-3">
                                    <span className="font-semibold text-tealDeep">🎤 Speaker:</span> {sermon.speaker}
                                </p>
                                <p className="text-sm text-gray-600 mb-4">
                                    <span className="font-semibold text-tealDeep">📅 Date:</span> {new Date(sermon.sermonDate).toLocaleDateString()}
                                </p>
                                {sermon.audioUrl && (
                                    <div className="mb-3">
                                        <audio controls className="w-full" style={{accentColor: '#0F766E'}}>
                                            <source src={sermon.audioUrl} type="audio/mpeg" />
                                            Your browser does not support the audio element.
                                        </audio>
                                    </div>
                                )}
                                {sermon.videoUrl && (
                                    <div className="mb-3">
                                        <video controls className="w-full max-h-48">
                                            <source src={sermon.videoUrl} type="video/mp4" />
                                            Your browser does not support the video element.
                                        </video>
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

