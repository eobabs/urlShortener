import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import UrlForm from './components/UrlForm';
import UrlList from './components/UrlList';
import UrlDetails from './components/UrlDetails';
import Navbar from './components/Navbar';
import NotFound from './components/NotFound';

function App() {
  const [urls, setUrls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUrls();
  }, []);

  const fetchUrls = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:7034/api/urls');
      if (!response.ok) {
        throw new Error('Failed to fetch URLs');
      }
      const data = await response.json();
      setUrls(data);
      setError(null);
    } catch (err) {
      setError('Error fetching URLs: ' + err.message);
      console.error('Error fetching URLs:', err);
    } finally {
      setLoading(false);
    }
  };

  const addUrl = (newUrl) => {
    setUrls([newUrl, ...urls]);
  };

  return (
    <Router>
      <div className="app">
        <Navbar />
        <div className="container">
          <Routes>
            <Route 
              path="/" 
              element={
                <>
                  <UrlForm addUrl={addUrl} />
                  <UrlList urls={urls} loading={loading} error={error} refreshUrls={fetchUrls} />
                </>
              } 
            />
            <Route path="/url/:shortCode" element={<UrlDetails />} />
            <Route path="/error" element={<NotFound />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;