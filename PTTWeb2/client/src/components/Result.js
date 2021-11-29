import {
  Button,
  List,
  ListSubheader,
  ListItemText,
  ListItemIcon,
  ListItem,
} from "@mui/material";
import AssessmentIcon from "@mui/icons-material/Assessment";

const timeRepresent = (time) => {
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
    " " +
    hours.substr(-2) +
    ":" +
    minutes.substr(-2)
  );
};

export default function Result({
  data,
  totalHour,
  totalPomodoro,
  setReportReady,
  reset,
}) {
  const handleClose = () => {
    setReportReady(false);
    reset();
  };

  return (
    <List sx={{ width: "100%" }}>
      <List
        sx={{ width: "100%" }}
        subheader={<ListSubheader>Sessions List</ListSubheader>}
      >
        {data.sessions.map((session) => (
          <ListItem sx={{ width: "100%" }} divider>
            <ListItemIcon>
              <AssessmentIcon />
            </ListItemIcon>
            <ListItemText
              primary={`Start Time: ${timeRepresent(session.startingTime)}`}
            />
            <ListItemText
              primary={`End Time: ${timeRepresent(session.endingTime)}`}
            />
            <ListItemText
              primary={`Hours worked: ${session.hoursWorked.toFixed(2)}`}
            />
          </ListItem>
        ))}
      </List>
      {totalHour && (
        <ListItem>
          <ListItemIcon>
            <AssessmentIcon />
          </ListItemIcon>
          <ListItemText
            primary={`Total Hour Worked: ${data.totalHoursWorkedOnProject.toFixed(
              2
            )}`}
          />
        </ListItem>
      )}
      {totalPomodoro && (
        <ListItem>
          <ListItemIcon>
            <AssessmentIcon />
          </ListItemIcon>
          <ListItemText
            primary={`Total Completed Pomodoro: ${data.completedPomodoros}`}
          />
        </ListItem>
      )}

      <Button
        style={{ marginTop: 20 }}
        onClick={handleClose}
        variant="outlined"
      >
        Close
      </Button>
    </List>
  );
}
