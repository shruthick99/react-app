# Step 1: Use Node.js for building
FROM node:18-alpine AS build

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy package.json and package-lock.json
COPY package*.json ./

# Step 4: Clean and install dependencies
RUN npm cache clean --force
RUN rm -rf node_modules package-lock.json
RUN npm install react-scripts@5.0.1 --save
RUN npm install

# Step 5: Copy application source code
COPY . .

# Step 6: Build the React application
RUN npm run build

# Step 7: Use Nginx for serving the app
FROM nginx:1.21-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
