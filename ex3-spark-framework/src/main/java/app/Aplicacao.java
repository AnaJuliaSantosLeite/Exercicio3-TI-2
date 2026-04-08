package app;

import static spark.Spark.*;
import service.ProdutoService;

public class Aplicacao {
    private static ProdutoService produtoService = new ProdutoService();
    
    public static void main(String[] args) {
        port(6789);
        staticFiles.location("/public");

        get("/produto/list/:orderby", (request, response) -> produtoService.getAll(request, response));
        get("/produto/:id", (request, response) -> produtoService.get(request, response));
        get("/produto/delete/:id", (request, response) -> produtoService.delete(request, response));
        post("/produto/insert", (request, response) -> produtoService.insert(request, response));

        get("/", (request, response) -> {
            response.redirect("/produto/list/1");
            return null;
        });
    }
}