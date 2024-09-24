import fastify from 'fastify'

const server = fastify()

server.post('/post', async (request, reply) => {
    return {
        body: request.body,
        headers: request.headers,
        query: request.query,
        params: request.params,
    }
})

server.listen({ port: 8080, host: '172.28.10.189' }, (err, address) => { // Remember to change host to your IP
    if (err) {
        console.error(err)
        process.exit(1)
    }
    console.log(`Server listening at ${address}`)
})
