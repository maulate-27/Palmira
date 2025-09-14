from PIL import Image

# Função para esconder uma mensagem
def encode_image(image_path, message, output_path):
    img = Image.open(image_path)
    encoded = img.copy()
    width, height = img.size
    index = 0

    # Converter mensagem para binário e adicionar delimitador
    binary_message = ''.join([format(ord(i), "08b") for i in message]) + "1111111111111110"

    for row in range(height):
        for col in range(width):
            if index < len(binary_message):
                r, g, b = img.getpixel((col, row))
                # alterar apenas o último bit do azul
                b = (b & ~1) | int(binary_message[index])
                encoded.putpixel((col, row), (r, g, b))
                index += 1
    encoded.save(output_path)
    print(f"Mensagem escondida em {output_path}")

# ----------------------------
# USO:
# ----------------------------
# Esconder
encode_image("dsa.png", "Maulate irah um dia tera uma namorada bem bonita e inteligente e juntos irao casar na igreja e terao lindos filhos, porque ele reza e  é bom em Python!", "imagem_com_mensagem.png")

