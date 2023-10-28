import axios from "axios";
import authHeader from "./auth-header";

const SERVICE_CONTEXT_ROOT = '/producer';
const ACCESS_TOKEN = 'access_token';

const instance = axios.create({
  baseURL: SERVICE_CONTEXT_ROOT
});

instance.interceptors.request.use((request) => {
  request.headers = authHeader();
  return request;
});

instance.interceptors.response.use((response) => {
  return response;
}, (error) => {

  if(error.status === 401) {
    localStorage.removeItem(ACCESS_TOKEN);
    window.location='/';
    return error;
  }

  if(error.response.data && error.response.data.errors) {
    error.response.data.errors.map( x => {
    }
  );
  } else {
    if(error.response.data && error.response.data.message) {
    } else {
    }  
  }
  return Promise.reject(error);
});

export default instance;