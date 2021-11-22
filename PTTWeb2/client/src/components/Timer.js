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

const getTime = (time) => {
  const date = new Date(time);
  const year = date.getFullYear();
  const month = "0" + (date.getMonth() + 1);
  const day = "0" + date.getDate();
  // Hours part from the timestamp
  const hours = "0" + date.getHours();
  // Minutes part from the timestamp;
  const minutes = "0" + date.getMinutes();
  // Will display time in 2019-02-19T10:30Z format
  return (
    year +
    "-" +
    month.substr(-2) +
    "-" +
    day.substr(-2) +
    "T" +
    hours.substr(-2) +
    ":" +
    minutes.substr(-2) +
    "Z"
  );
};

export default function Timer({
  countdown,
  setCountdown,
  isRest,
  setIsRest,
  setIsTimer,
  setContinueDialog,
  projectId,
  setEndTime,
  counter,
  setCounter,
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
      if (projectId !== "") {
        setCounter(counter + 1);
        setEndTime(getTime(Date.now() + 30 * 1000 * 60));
        setContinueDialog(true);
      }
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
