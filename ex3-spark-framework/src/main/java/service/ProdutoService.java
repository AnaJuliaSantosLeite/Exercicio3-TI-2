package service;

import java.util.Scanner;
import java.time.LocalDate;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import dao.ProdutoDAO;
import model.Produto;
import spark.Request;
import spark.Response;

public class ProdutoService {
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private String form;

    public ProdutoService() { makeForm(); }

    public void makeForm() { makeForm(1, new Produto(), 1); }

    public void makeForm(int tipo, Produto produto, int orderBy) {
        form = "";
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("form.html");
            Scanner scanner = new Scanner(is, "UTF-8");
            while(scanner.hasNextLine()) form += scanner.nextLine() + "\n";
            scanner.close();
        } catch (Exception e) { form = "Erro ao ler form.html"; }

        String umProduto = "<h3>" + (produto.getID() == -1 ? "Inserir Produto" : "Atualizar Produto") + "</h3>"
                + "<form action=\"/produto/" + (produto.getID() == -1 ? "insert" : "update/"+produto.getID()) + "\" method=\"post\">"
                + "<div class=\"row\">"
                + "  <div class=\"card\">Descrição: <input type=\"text\" name=\"descricao\" value=\""+produto.getDescricao()+"\" placeholder=\"leite, pão...\"></div>"
                + "  <div class=\"card\">Preço: <input type=\"text\" name=\"preco\" value=\""+produto.getPreco()+"\"></div>"
                + "  <div class=\"card\">Quantidade: <input type=\"text\" name=\"quantidade\" value=\""+produto.getQuantidade()+"\"></div>"
                + "  <div class=\"card\">Data de fabricação: <input type=\"text\" name=\"dataFabricacao\" value=\""+produto.getDataFabricacao()+"\"></div>"
                + "  <div class=\"card\">Data de validade: <input type=\"text\" name=\"dataValidade\" value=\""+produto.getDataValidade()+"\"></div>"
                + "</div>"
                + "<button type=\"submit\" class=\"btn-add\">" + (produto.getID() == -1 ? "cadastrar" : "atualizar") + "</button>"
                + "</form>";
        
        form = form.replaceFirst("<UM-PRODUTO>", umProduto);

        List<Produto> lista = (orderBy == 3) ? produtoDAO.getOrderByPreco() : produtoDAO.get();
        String listHtml = "<table><tr><th>ID</th><th>Descrição</th><th>Preço</th><th>Quantidade</th></tr>";
        for (Produto p : lista) {
            listHtml += "<tr><td>"+p.getID()+"</td><td>"+p.getDescricao()+"</td><td>"+p.getPreco()+"</td><td>"+p.getQuantidade()+"</td></tr>";
        }
        listHtml += "</table>";
        
        form = form.replaceFirst("<LISTAR-PRODUTO>", listHtml);
    }

    public Object getAll(Request request, Response response) {
        int order = Integer.parseInt(request.params(":orderby"));
        makeForm(1, new Produto(), order);
        return form;
    }

    public Object insert(Request request, Response response) {
        Produto p = new Produto(-1, request.queryParams("descricao"), Float.parseFloat(request.queryParams("preco")), 
                    Integer.parseInt(request.queryParams("quantidade")), 
                    LocalDateTime.parse(request.queryParams("dataFabricacao")), LocalDate.parse(request.queryParams("dataValidade")));
        produtoDAO.insert(p);
        response.redirect("/produto/list/1");
        return null;
    }

    public Object get(Request request, Response response) {
        Produto p = produtoDAO.get(Integer.parseInt(request.params(":id")));
        if (p != null) makeForm(2, p, 1);
        else makeForm();
        return form;
    }

    public Object delete(Request request, Response response) {
        produtoDAO.delete(Integer.parseInt(request.params(":id")));
        response.redirect("/produto/list/1");
        return null;
    }
}