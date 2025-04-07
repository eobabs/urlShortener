import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { format } from 'date-fns';

function UrlDetails() {
  const { shortCode } = useParams();
  const navigate = useNavigate();
  const [url, setUrl] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUrlDetails = async () => {
      try {
        setLoading(true);
        const response = await fetch(`http://localhost:7034/api/urls/${shortCode}`);
        
        if (!response.ok) {
          if (response.status === 404) {
            navigate('/error');
            return;
          }
          throw new Error('Failed to fetch URL details');
        }
        
        const data = await response.json();
        setUrl(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUrlDetails();
  }, [shortCode, navigate]);

  if (loading) {
    return <div className="loading">Loading URL details...</div>;
  }

  if (error) {
    return <div className="error-container">{error}</div>;
  }

  if (!url) {
    return <div className="not-found">URL not found</div>;
  }

  return (
    <div className="url-details">
      <h2>URL Details</h2>
      <div className="details-card">
        <div className="detail-row">
          <h3>Short URL:</h3>
          <p>
            <a 
              href={`http://localhost:7034/${url.shortCode}`} 
              target="_blank" 
              rel="noopener noreferrer"
            >
              {`${window.location.origin}/${url.shortCode}`}
            </a>
            <button 
              className="btn-copy"
              onClick={() => navigator.clipboard.writeText(`${window.location.origin}/${url.shortCode}`)}
            >
              Copy
            </button>
          </p>
        </div>
        
        <div className="detail-row">
          <h3>Original URL:</h3>
          <p>
            <a href={url.originalUrl} target="_blank" rel="noopener noreferrer">
              {url.originalUrl}
            </a>
          </p>
        </div>
        
        <div className="detail-row">
          <h3>Created:</h3>
          <p>{format(new Date(url.createdAt), 'PPP pp')}</p>
        </div>
        
        {url.expiresAt && (
          <div className="detail-row">
            <h3>Expires:</h3>
            <p>{format(new Date(url.expiresAt), 'PPP pp')}</p>
          </div>
        )}
        
        <div className="detail-row">
          <h3>Total Clicks:</h3>
          <p className="click-count">{url.clickCount}</p>
        </div>
      </div>
      
      <button className="btn btn-secondary" onClick={() => navigate('/')}>
        Back to Home
      </button>
    </div>
  );
}

export default UrlDetails;