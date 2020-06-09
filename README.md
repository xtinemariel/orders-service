# orders-service

## Starting up the application
1. Make sure Docker is installed and running on your machine.
2. Run start.sh at the base directory of the project.

  **NOTE:** If you want to use your own Google API Key, please update this property in application.properties file before starting the application:

```
api.google.key=<API_KEY>
```

## APIs

### 1.  Create Order
 - HTTP Method: POST
 - URL path: /orders
 - Sample Request Body:
 
```
{
    "origin": ["14.492447","121.014916"],
	"destination": ["14.493829","121.014562"]
}
```

- Sample Response:

```
{
    "id": "1",
    "distance": 175,
    "status": "UNASSIGNED"
}
```

### 2. Take Order
 - HTTP Method: PATCH
 - URL path: /orders/{id}
 - Sample Request Body:
 
```
{
    "status": "TAKEN"
}
```
- Sample Response:

```
{
    "status": "SUCCESS"
}
```

###3. Get Orders
 - HTTP Method: GET
 - URL path: /orders?page={page}&limit={limit}
 - Sample Request:

```
http://localhost:8080/orders?page=1&limit=4
```
- Sample Response:

```
[
    {
        "id": "1",
        "distance": 14412,
        "status": "TAKEN"
    },
    {
        "id": "2",
        "distance": 122,
        "status": "UNASSIGNED"
    }
]
```