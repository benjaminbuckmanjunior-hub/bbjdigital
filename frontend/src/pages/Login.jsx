
export default function Login() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-primary">
      <div className="bg-white text-gray-800 p-10 rounded-2xl shadow-2xl w-full max-w-md">
        <h2 className="text-3xl font-bold text-primary mb-6 text-center">
          Member Login
        </h2>

        <input
          type="email"
          placeholder="Email Address"
          className="w-full mb-4 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-accent"
        />

        <input
          type="password"
          placeholder="Password"
          className="w-full mb-6 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-accent"
        />

        <button className="w-full bg-accent hover:bg-accent-hover text-primary font-semibold py-3 rounded-lg transition">
          Login
        </button>
      </div>
    </div>
  );
}
