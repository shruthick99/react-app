# Step 1: Use an official Node image as the base image
FROM node:18-alpine AS build

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy package.json and package-lock.json for caching
COPY package*.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy the rest of the application source code
COPY . .

# Step 6: Build the React app for production
RUN npm run build

# Step 7: Install the "serve" package globally to serve the app
RUN npm install -g serve

# Step 8: Set the working directory to the build folder
WORKDIR /app/build

# Step 9: Expose port 80
EXPOSE 80

# Step 10: Serve the built React app
CMD ["serve", "-s", ".", "-l", "80"]
