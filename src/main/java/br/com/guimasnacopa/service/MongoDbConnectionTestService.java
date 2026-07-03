package br.com.guimasnacopa.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bson.Document;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

@Service
public class MongoDbConnectionTestService {

	public MongoConnectionTestResult testarConexao(String uri) {
		if (uri == null || uri.trim().isEmpty()) {
			return MongoConnectionTestResult.erro("A URI do MongoDB deve ser informada.");
		}

		MongoClient mongoClient = null;
		try {
			MongoClientURI mongoClientUri = new MongoClientURI(uri.trim());
			mongoClient = new MongoClient(mongoClientUri);
			String databaseName = mongoClientUri.getDatabase() == null ? "admin" : mongoClientUri.getDatabase();
			MongoDatabase database = mongoClient.getDatabase(databaseName);
			database.runCommand(new Document("ping", 1));
			return MongoConnectionTestResult.sucesso("Conexao testada com sucesso.");
		} catch (Exception e) {
			return MongoConnectionTestResult.erro(getStackTrace(e));
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static class MongoConnectionTestResult {

		private final boolean sucesso;
		private final String mensagem;
		private final String stacktrace;

		private MongoConnectionTestResult(boolean sucesso, String mensagem, String stacktrace) {
			this.sucesso = sucesso;
			this.mensagem = mensagem;
			this.stacktrace = stacktrace;
		}

		public static MongoConnectionTestResult sucesso(String mensagem) {
			return new MongoConnectionTestResult(true, mensagem, null);
		}

		public static MongoConnectionTestResult erro(String stacktrace) {
			return new MongoConnectionTestResult(false, null, stacktrace);
		}

		public boolean isSucesso() {
			return sucesso;
		}

		public String getMensagem() {
			return mensagem;
		}

		public String getStacktrace() {
			return stacktrace;
		}
	}
}