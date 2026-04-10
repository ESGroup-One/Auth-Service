import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Register from './pages/Auth/Register';
import Login from './pages/Auth/Login';
import ProtectedRoute from './components/ProtectedRoute';

// Simple placeholder components so your app doesn't crash while testing
const StudentDashboard = () => <div className="p-10 text-2xl font-bold text-center">Student Dashboard 🎓</div>;
const AdminDashboard = () => <div className="p-10 text-2xl font-bold text-center">Admin Dashboard 💼</div>;
const SuperadminDashboard = () => <div className="p-10 text-2xl font-bold text-center text-red-600">Superadmin Dashboard 👑</div>;

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes - Anyone can access these */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

        {/* Protected Routes for STUDENTS */}
        {/* Only users with the 'student' role can pass this check */}
        <Route element={<ProtectedRoute allowedRoles={['student']} />}>
          <Route path="/student-dashboard" element={<StudentDashboard />} />
          {/* Add more student-only routes here later, e.g., /grades, /profile */}
        </Route>

        {/* Protected Routes for ADMINS */}
        <Route element={<ProtectedRoute allowedRoles={['admin']} />}>
          <Route path="/admin-dashboard" element={<AdminDashboard />} />
        </Route>

        {/* Protected Routes for SUPERADMINS */}
        <Route element={<ProtectedRoute allowedRoles={['superadmin']} />}>
          <Route path="/superadmin-dashboard" element={<SuperadminDashboard />} />
        </Route>
        
        {/* Fallback route for 404 Not Found */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;