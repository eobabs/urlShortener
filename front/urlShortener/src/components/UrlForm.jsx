import React, { useState } from 'react';

function UrlForm({ addUrl }) {
  const [formData, setFormData] = useState({
    originalUrl: '',
    validityDays: 30
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: name === 'validityDays' ? Number(value) : value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    setSuccess(null);

    try {
      const response = await fetch('http://localhost:7034/api/urls', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.originalUrl || data.error || 'Failed to create short URL');
      }

      addUrl(data);
      setSuccess(`URL successfully shortened! Your short URL is: ${window.location.origin}/${data.shortCode}`);
      setFormData({ originalUrl: '', validityDays: 30 });
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="url-form-container">
      <h2>Shorten Your URL</h2>
      <form onSubmit={handleSubmit} className="url-form">
        <div className="form-group">
          <label htmlFor="originalUrl">Enter your long URL:</label>
          <input
            type="url"
            id="originalUrl"
            name="originalUrl"
            value={formData.originalUrl}
            onChange={handleChange}
            placeholder="https://example.com/your-long-url"
            required
            className="form-control"
          />
        </div>
        <div className="form-group">
          <label htmlFor="validityDays">URL validity (days):</label>
          <input
            type="number"
            id="validityDays"
            name="validityDays"
            value={formData.validityDays}
            onChange={handleChange}
            min="1"
            max="365"
            className="form-control"
          />
        </div>
        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
          {isSubmitting ? 'Shortening...' : 'Shorten URL'}
        </button>
      </form>

      {error && <div className="alert alert-error">{error}</div>}
      {success && (
        <div className="alert alert-success">
          <p>{success}</p>
          <button 
            className="btn btn-secondary"
            onClick={() => navigator.clipboard.writeText(`${window.location.origin}/${formData.shortCode}`)}
          >
            Copy URL
          </button>
        </div>
      )}
    </div>
  );
}

export default UrlForm;