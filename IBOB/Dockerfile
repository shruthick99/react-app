# Step 1: Use the official Node.js image
FROM node:18-alpine AS build

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy package.json and package-lock.json
COPY package*.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy the entire application
COPY . .

# Step 6: Build the application
RUN npm run build || (cat /app/package.json && exit 1)

# Step 7: Use an Nginx image to serve the app
FROM nginx:1.21-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80

# Step 8: Add a health check (optional)
HEALTHCHECK --interval=30s --timeout=5s \
  CMD curl -f http://localhost/ || exit 1

# Step 9: Start Nginx
CMD ["nginx", "-g", "daemon off;"]
