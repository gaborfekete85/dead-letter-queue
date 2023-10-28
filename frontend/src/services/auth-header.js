export default function authHeader() {
    const accessToken = localStorage.getItem("access_token");
    return { "Content-type": "application/json", Authorization: "Bearer " + accessToken };
}