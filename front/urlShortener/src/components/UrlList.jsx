import React from 'react';
import { Link } from 'react-router-dom';
import { formatDistanceToNow } from 'date-fns';

function UrlList({ urls, loading, error, refreshUrls }) {
  if (loading) {
    return <div className="loading">Loading URLs...</div>;
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button className="btn btn-secondary" onClick={refreshUrls}>Try Again</button>
      </div>
    );
  }

  if (urls.length === 0) {
    return <div className="no-urls">No URLs have been shortened yet.</div>;
  }

  return (
    <div className="url-list">
      <h2>Your Shortened URLs</h2>
      <div className="url-cards">
        {urls.map((url) => (
          <div key={url.id} className="url-card">
            <h3 className="short-url">
              <a 
                href={`http://localhost:7034/${url.shortCode}`} 
                target="_blank" 
                rel="noopener noreferrer"
              >
                {`${window.location.origin}/${url.shortCode}`}
              </a>
            </h3>
            <p className="original-url">
              Original: <span>{url.originalUrl}</span>
            </p>
            <div className="url-stats">
              <p>Created: {formatDistanceToNow(new Date(url.createdAt))} ago</p>
              {url.expiresAt && (
                <p>Expires: {formatDistanceToNow(new Date(url.expiresAt))} from now</p>
              )}
              <p>Clicks: {url.clickCount}</p>
            </div>
            <Link to={`/url/${url.shortCode}`} className="btn btn-outline">
              View Details
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}

export default UrlList;