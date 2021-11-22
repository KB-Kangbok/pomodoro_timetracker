import { useEffect, useRef, useState } from "react";

function useInterval(callback, delay) {
  const savedCallback = useRef();

  // Remember the latest callback.
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // Set up the interval.
  useEffect(() => {
    function tick() {
      savedCallback.current();
    }
    if (delay !== null) {
      let id = setInterval(tick, delay);
      return () => clearInterval(id);
    }
  }, [delay]);
}

export default function Timer({
  countdown,
  setCountdown,
  isRest,
  setIsRest,
  setIsTimer,
  counter,
  setCounter,
  sessionId,
  setSessionId,
  setContinueDialog,
}) {
  const initialMin = Math.floor(countdown / 60);
  const initialSec = countdown % 60;
  const [timerMinute, setMinute] = useState(initialMin);
  const [timerSecond, setSecond] = useState(initialSec);

  const updateTime = (countdown, minute, second) => {
    setCountdown(countdown);
    setMinute(minute);
    setSecond(second);
  };

  const endOfTimer = (countdown, minute, second) => {
    if (isRest) {
      setCounter(counter + 1);
      if (counter === 1) {
        //create new session and get sessionId and set it inside sessionId using setSessionId
      } else {
        //edit session with increased pomodoro counter and end time
      }
      setContinueDialog(true);
      setIsTimer(false);
    } else {
      setIsRest(true);
      updateTime(countdown, minute, second);
    }
  };

  useInterval(() => {
    const time = countdown - 1;
    const minute = Math.floor(time / 60);
    const second = time % 60;

    if (time >= 0) {
      updateTime(time, minute, second);
    } else {
      endOfTimer(3, 0, 3);
    }
  }, 1000);

  return (
    <div>
      <span>
        <p>{`${timerMinute}:${
          timerSecond < 10 ? "0" + timerSecond : timerSecond
        }`}</p>
      </span>
    </div>
  );
}
