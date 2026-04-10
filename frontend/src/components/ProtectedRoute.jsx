import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute = ({ allowedRoles }) => {
    const userString = localStorage.getItem('user');
    const authData = userString ? JSON.parse(userString) : null;

    if (!authData || !authData.token) {
        return <Navigate to="/login" replace />;
    }

    const userRole = authData.user.role?.toLowerCase() || 'student';

    if (allowedRoles && !allowedRoles.includes(userRole)) {

        if (userRole === 'superadmin') return <Navigate to="/superadmin-dashboard" replace />;
        if (userRole === 'admin') return <Navigate to="/admin-dashboard" replace />;
        return <Navigate to="/student-dashboard" replace />;
    }
    return <Outlet />;
};

export default ProtectedRoute;