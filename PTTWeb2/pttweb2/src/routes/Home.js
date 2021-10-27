import axios from "axios";
import { useEffect, useState } from "react";
import Login from "../components/Login";
import { apiUrl } from "../config.json";

const useInput = (initialValue) => {
  const [value, setValue] = useState(initialValue);
  const onChange = (event) => {
    const {
      target: { value },
    } = event;
    setValue(value);
  };
  return { value, onChange };
};

function Home() {
  const username = useInput("");
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const getUsers = async () => {
      const { data } = await axios.get(`${apiUrl}/users`);
      // This is for test-case, later uncomment axios part and delete this sentence
      // const data = [
      //   {
      //     id: 1,
      //     firstName: "KB",
      //     lastName: "Lee",
      //     email: "klee869@gatech.edu",
      //   },
      //   {
      //     id: 2,
      //     firstName: "Heejoo",
      //     lastName: "Cho",
      //     email: "joheeju@gatech.edu",
      //   },
      // ];

      setUsers(data);
    };
    getUsers();
  }, []);
  return (
    <div>
      <Login
        username={username.value}
        handleChange={username.onChange}
        users={users}
      />
    </div>
  );
}

export default Home;
