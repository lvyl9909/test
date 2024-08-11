import logo from './logo.svg';
import './App.css';
import {RouterProvider} from 'react-router-dom'
import router from "./router";
import React, { useState } from 'react';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

function App() {
  // const [data, setData] = useState(null);
  // const [loading, setLoading] = useState(false);
  //
  // const fetchData = async () => {
  //   setLoading(true);
  //   setData(null);
  //   try {
  //     const res = await fetch(
  //         `${API_BASE_URL}/students`,
  //     );
  //     if (res.status > 299) {
  //       setData(`woops! bad response status ${res.status} from API`);
  //       return;
  //     }
  //     setData(await res.text());
  //   } catch (e) {
  //     setData(`woops! an error occurred: ${e}`);
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  return (
      <div className ="app">
        <RouterProvider router={router}/>
      </div>
      // <>
      //   {data && <p>{data}</p>}
      //   {loading && <p>loading...</p>}
      //   <button
      //       disabled={loading}
      //       type="button"
      //       onClick={fetchData}
      //   >
      //     Fetch data
      //   </button>
      // </>
  );
}

export default App;