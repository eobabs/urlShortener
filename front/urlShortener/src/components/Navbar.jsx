import React from 'react';
import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav className="navbar">
      <div className="container">
        <Link to="/" className="logo">
          <h1>ByteURL</h1>
        </Link>
        <div className="nav-links">
          <Link to="/" className="nav-link">Home</Link>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;