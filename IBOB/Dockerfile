# Step 1: Use an official Node image as the base image
FROM node:18-alpine AS build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy package.json and package-lock.json for dependency installation
COPY package*.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy the application source code
COPY . .

# Step 6: Build the React app
RUN npm run build

# Step 7: Use a lightweight Nginx server to serve the app in production
FROM nginx:1.21-alpine AS production

# Step 8: Copy the build output from the previous stage
COPY --from=build /app/build /usr/share/nginx/html

# Step 9: Expose port 80
EXPOSE 80

# Step 10: Start Nginx server
CMD ["nginx", "-g", "daemon off;"]
