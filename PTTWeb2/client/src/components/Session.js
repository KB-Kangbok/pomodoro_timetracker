import Timer from "../assets/timer.png";
import axios from "axios";
import { apiUrl } from "../config.json";
import {useState } from "react";

function Session({user, project}) {
    const [session, setSession] = useState([{ startTime: Date().toLocaleString("en-US", {timeZone: "America/New_York"}), counter: 0, endTime: 0 }]);


    const handleSession = async () => {
        setSession({endTime: Date.now()});
        try {
          const res = await axios.post(
            `${apiUrl}/users/${user}/projects/${project}/sessions`, session
          );
          console.log(res.data.id);
        } catch (e) {
          if (e.response.status === 404) {
            alert(`Bad request`);
          }
        }
      };

      function getTime(time) {
        var date = new Date(time);
        // Hours part from the timestamp
        var hours = date.getHours();
        // Minutes part from the timestamp
        var minutes = "0" + date.getMinutes();
        // Seconds part from the timestamp
        var seconds = "0" + date.getSeconds();
        var ampm = hours >= 12 ? 'PM' : 'AM';
        // Will display time in 10:30:23 format
        return hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2) + ampm;
      }

  return (
      
    <div style={{display: "flex", flexDirection: "row"}}>
        {/* <img src={Timer} width="40%" alt="timer"/> */}
        <div>
            <h3>You started a session at {getTime(session.map((data) => data.startTime))}.
            </h3>
            <h3>This session has {session.map((data) => data.counter)} pomodoros.
            </h3>
        </div>
    </div>
  );
}

export default Session;
