const crypto = require("node:crypto");

const password = process.argv[2];
const salt = crypto.randomBytes(16);
const hash = crypto.pbkdf2Sync(password, salt, 150000, 32, "sha256");

console.log(
  "pbkdf2-sha256$150000$" +
  salt.toString("base64") +
  "$" +
  hash.toString("base64")
);
