import { Link } from "react-router-dom";

function Navigation() {
  return (
    <div>
      <Link to="/">Home</Link>
      <Link to="/admin">Admin</Link>
      <Link to="/user">User</Link>
    </div>
  );
}

export default Navigation;
