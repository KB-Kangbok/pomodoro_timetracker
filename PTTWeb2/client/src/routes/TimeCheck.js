import { useState, useEffect } from "react";
import Timer from "../components/Timer";

function TimeCheck() {
  const [countdown, setCountdown] = useState(10);
  const [isRest, setIsRest] = useState(false);
  const [isTimer, setIsTimer] = useState(false);
  const handleClick = () => {
    setIsRest(false);
    setIsTimer(true);
    setCountdown(5);
  };

  return (
    <div>
      <button onClick={handleClick}>start</button>
      {isTimer && (
        <Timer
          countdown={countdown}
          setCountdown={setCountdown}
          isRest={isRest}
          setIsRest={setIsRest}
          setIsTimer={setIsTimer}
        />
      )}
    </div>
  );
}

export default TimeCheck;
