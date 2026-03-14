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

    if (loading) return <Layout><div className="text-center py-8 text-lemon font-semibold">Loading...</div></Layout>;

    return (
        <Layout>
            <div className="max-w-4xl mx-auto py-8">
                <h1 className="text-4xl font-bold mb-2 text-lemon">🎤 Sermon Library</h1>
                <p className="text-white mb-8">Watch and listen to our latest sermons</p>
                {sermons.length === 0 ? (
                    <div className="bg-teal-800 border-l-4 border-lemon p-8 rounded-lg text-center">
                        <p className="text-white text-lg">No sermons available yet.</p>
                        <p className="text-gray-300 mt-2">New sermons will be added soon. Stay tuned!</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {sermons.map((sermon, index) => (
                            <div key={sermon.id || index} className="bg-teal-800 p-6 rounded-lg border-l-4 border-lemon shadow-md hover:shadow-lg transition">
                                <h3 className="text-2xl font-semibold text-lemon mb-2">{sermon.title}</h3>
                                <p className="text-white mb-4">{sermon.description}</p>
                                <p className="text-sm text-gray-300 mb-3">
                                    <span className="font-semibold text-lemon">🎤 Speaker:</span> {sermon.speaker}
                                </p>
                                <p className="text-sm text-gray-300 mb-4">
                                    <span className="font-semibold text-lemon">📅 Date:</span> {new Date(sermon.sermonDate).toLocaleDateString()}
                                </p>
                                {sermon.audioUrl && (
                                    <div className="mb-4">
                                        <audio controls className="w-full" style={{accentColor: '#F4D03F'}}>
                                            <source src={sermon.audioUrl} type="audio/mpeg" />
                                            Your browser does not support the audio element.
                                        </audio>
                                        <a
                                            href={sermon.audioUrl}
                                            download
                                            className="inline-block mt-2 bg-lemon text-tealDeep px-4 py-2 rounded font-semibold hover:bg-yellow-400 transition text-sm"
                                        >
                                            📥 Download Audio
                                        </a>
                                    </div>
                                )}
                                {sermon.videoUrl && (
                                    <div className="mb-4">
                                        <video controls className="w-full max-h-48">
                                            <source src={sermon.videoUrl} type="video/mp4" />
                                            Your browser does not support the video element.
                                        </video>
                                        <a
                                            href={sermon.videoUrl}
                                            download
                                            className="inline-block mt-2 bg-lemon text-tealDeep px-4 py-2 rounded font-semibold hover:bg-yellow-400 transition text-sm"
                                        >
                                            📥 Download Video
                                        </a>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}}
            </div>
        </Layout>
    );
}

