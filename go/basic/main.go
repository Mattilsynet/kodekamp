package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type ActionRequest struct {
	Action string `json:"action"`
}

type ActionResponse struct {
	Message string `json:"message"`
}

func main() {
	mux := http.NewServeMux()
	fileServer := http.FileServer(http.Dir("./static/"))
	mux.Handle("/", fileServer)

	mux.HandleFunc("POST /action", handlerAction)

	http.ListenAndServe("localhost:8080", mux)
}

func handlerAction(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method Not Allowed", http.StatusMethodNotAllowed)
		return
	}

	// Set the content type to JSON for the response
	w.Header().Set("Content-Type", "application/json")

	// Parse the JSON body
	var actionReq ActionRequest
	err := json.NewDecoder(r.Body).Decode(&actionReq)
	if err != nil {
		http.Error(w, "Bad Request", http.StatusBadRequest)
		return
	}

	// Use the parsed data (for example, print it or process it)
	fmt.Printf("Received action: %s\n", actionReq.Action)

	// Create a response message
	response := ActionResponse{
		Message: "Action received: " + actionReq.Action,
	}

	// Encode the response as JSON and send it
	json.NewEncoder(w).Encode(response)
}
