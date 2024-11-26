package com.example.la_dfe.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.la_dfe.entitites.Empresa;
import com.example.la_dfe.entitites.NotaEntrada;
import com.example.la_dfe.exception.SistemaException;
import com.example.la_dfe.repository.EmpresaRepository;
import com.example.la_dfe.utils.ArquivoUtil;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.Evento;
import br.com.swconsultoria.nfe.dom.enuns.ConsultaDFeEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.dom.enuns.ManifestacaoEnum;
import br.com.swconsultoria.nfe.dom.enuns.PessoaEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema.resnfe.ResNFe;
import br.com.swconsultoria.nfe.schema.retdistdfeint.RetDistDFeInt;
import br.com.swconsultoria.nfe.schema_4.consReciNFe.TNfeProc;
import br.com.swconsultoria.nfe.util.ManifestacaoUtil;
import br.com.swconsultoria.nfe.util.ObjetoUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import br.com.swconsultoria.nfe.schema.envConfRecebto.TEnvEvento;
import br.com.swconsultoria.nfe.schema.envConfRecebto.TRetEnvEvento;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DistribuicaoService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private NotaEntradaService notaEntradaService;

    public void consultaNotas()
            throws NfeException, CertificadoException, IOException, JAXBException, SistemaException {
        List<Empresa> empresas = empresaRepository.findAll();

        for (Empresa empresa : empresas) {
            efetuaConsulta(empresa);
        }
    }

    private void efetuaConsulta(Empresa empresa)
            throws NfeException, CertificadoException, IOException, JAXBException, SistemaException {

        // Cria a configuração da NFe
        ConfiguracoesNfe configuracao = criaConfiguracao(empresa);

        // 2 Listas, 1 para manifestar e outra para salvar
        List<String> listaNotasManifestar = new ArrayList<>();
        List<NotaEntrada> listaNotasSalvar = new ArrayList<>();

        boolean existeMais = true;

        // Vai consultar as notas até não existir mais
        while (existeMais) {
            // Consulta as notas

            log.info("Iniciando distribuição DFe para empresa: {}", empresa.getCpfCnpj());
            log.info("NSU: {}", ObjetoUtil.verifica(empresa.getNsu()).orElse("000000000000000"));

            RetDistDFeInt retorno = Nfe.distribuicaoDfe(configuracao, PessoaEnum.JURIDICA, empresa.getCpfCnpj(),
                    ConsultaDFeEnum.NSU, ObjetoUtil.verifica(empresa.getNsu()).orElse("000000000000000"));

            // Verifica se houve erro na consulta
            if (!retorno.getCStat().equals(StatusEnum.DOC_LOCALIZADO_PARA_DESTINATARIO.getCodigo())) {
                if (retorno.getCStat().equals(StatusEnum.CONSUMO_INDEVIDO.getCodigo())) {
                    // break para sair do while.
                    break;
                } else {
                    throw new SistemaException(
                            "Erro ao consultar notas: " + retorno.getCStat() + " - " + retorno.getXMotivo());
                }
            }

            populaLista(empresa, listaNotasManifestar, listaNotasSalvar, retorno);

            // Se os 2 NSU forem iguais, não existe mais notas
            existeMais = !retorno.getUltNSU().equals(retorno.getMaxNSU());
            empresa.setNsu(retorno.getUltNSU());
        }

        empresaRepository.save(empresa);
        notaEntradaService.salvarNotas(listaNotasSalvar);
        manifestaListaNotas(listaNotasManifestar, empresa, configuracao);

        // TODO VERIFICAR LISTA E MANIFESTAR

    }

    private void populaLista(Empresa empresa, List<String> listaNotasManifestar, List<NotaEntrada> listaNotasSalvar,
            RetDistDFeInt retorno) throws IOException, JAXBException {
        for (RetDistDFeInt.LoteDistDFeInt.DocZip doc : retorno.getLoteDistDFeInt().getDocZip()) {
            String xml = XmlNfeUtil.gZipToXml(doc.getValue());
            log.info("Xml: " + xml);
            log.info("Schema: " + doc.getSchema());
            log.info("NSU: " + doc.getNSU());

            switch (doc.getSchema()) {
                // Tratar o XML conforme o Schema
                case "resNFe_v1.01.xsd":
                    ResNFe resNFe = XmlNfeUtil.xmlToObject(xml, ResNFe.class);
                    String chave = resNFe.getChNFe();

                    listaNotasManifestar.add(chave);
                    break;
                case "procNFe_v4.00.xsd": // Arquivo Schema
                    TNfeProc nfe = XmlNfeUtil.xmlToObject(xml, TNfeProc.class);
                    NotaEntrada notaEntrada = new NotaEntrada();
                    notaEntrada.setChave(nfe.getNFe().getInfNFe().getId().substring(3));
                    notaEntrada.setEmpresa(empresa);
                    notaEntrada.setSchema(doc.getSchema());
                    notaEntrada.setCnpjEmitente(nfe.getNFe().getInfNFe().getEmit().getCNPJ());
                    notaEntrada.setNomeEmitente(nfe.getNFe().getInfNFe().getEmit().getXNome());
                    notaEntrada.setValor(new BigDecimal(nfe.getNFe().getInfNFe().getTotal().getICMSTot().getVNF()));
                    notaEntrada.setXml(ArquivoUtil.compactaXml(xml));
                    listaNotasSalvar.add(notaEntrada);
                default:
                    break;
            }
        }
    }

    public void manifestaListaNotas(List<String> chaves, Empresa empresa, ConfiguracoesNfe configuracoesNfe)
            throws NfeException {

        for (String chave : chaves) {
            Evento manifesta = new Evento();

            manifesta.setChave(chave);
            manifesta.setCnpj(empresa.getCpfCnpj());
            manifesta.setMotivo("Manifestacao notas Resumo");
            manifesta.setDataEvento(LocalDateTime.now());
            manifesta.setTipoManifestacao(ManifestacaoEnum.CIENCIA_DA_OPERACAO);

            // Monta o Evento de Manifestação
            TEnvEvento enviEvento = ManifestacaoUtil.montaManifestacao(manifesta, configuracoesNfe);

            // Envia o Evento de Manifestação
            // Se False, podemos colocar o caminho do schemas vazio
            TRetEnvEvento retorno = Nfe.manifestacao(configuracoesNfe, enviEvento, false);

            if (!retorno.getRetEvento().get(0).getInfEvento().getCStat().equals(StatusEnum.EVENTO_VINCULADO)) {
                log.error("Erro ao manifestar a nota: " + retorno.getCStat() + " - " + retorno.getXMotivo());
            }
        }

    }

    public ConfiguracoesNfe criaConfiguracao(Empresa empresa) throws CertificadoException {

        Certificado certificado = CertificadoService.certificadoPfxBytes(empresa.getCertificado(),
                empresa.getSenhaCertificado());

        return ConfiguracoesNfe.criarConfiguracoes(
                EstadosEnum.valueOf(empresa.getUf()),
                empresa.getAmbiente(),
                certificado,
                "/c/teste/nfe/schemas");
    }
}
