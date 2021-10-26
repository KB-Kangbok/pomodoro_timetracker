function User({ location }) {
  const { firstName, lastName, id } = location.state;
  console.log(location.state);

  return <h1>{`Hi ${firstName} ${lastName}`}</h1>;
}

export default User;
